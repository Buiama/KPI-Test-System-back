package com.kpi.codeexecutionservice.services.implementations;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import com.kpi.codeexecutionservice.models.Assignment;
import com.kpi.codeexecutionservice.repositories.IAssignmentRepository;
import com.kpi.codeexecutionservice.services.interfaces.IDockerService;
import com.kpi.codeexecutionservice.services.strategies.BuildImageStrategy;
import com.kpi.codeexecutionservice.services.strategies.IDockerExecutionStrategy;
import com.kpi.codeexecutionservice.services.strategies.MountVolumeStrategy;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DockerService implements IDockerService {
    private final DockerClient dockerClient;
    private final Map<String, String> prebuiltImages = new HashMap<>();
    private final Map<String, String> usedStrategies = new ConcurrentHashMap<>();

    @Value("${docker.timeout:60}")
    private int defaultExecutionTimeoutSeconds;

    @Value("${docker.memory.limit:256}")
    private long defaultMemoryLimitMB;

    @Value("${docker.cpu.limit:1}")
    private int defaultCpuLimit;

    @Value("${app.temp.directory:temp}")
    private String tempDirectory;

    @Autowired
    private IAssignmentRepository assignmentRepository;

    private BuildImageStrategy buildImageStrategy;
    private MountVolumeStrategy mountVolumeStrategy;

    public DockerService() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        this.dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerHttpClient(httpClient)
                .build();
    }

    @PostConstruct
    private void initialize() {
        log.info("Initializing DockerService. Temp directory path from config: {}", tempDirectory);
        if (this.tempDirectory == null || this.tempDirectory.trim().isEmpty()) {
            this.tempDirectory = System.getProperty("user.dir") + File.separator + "temp_execution";
            log.warn("tempDirectory was null or empty, using fallback: {}", this.tempDirectory);
        }

        createTempDirectory();

        // Инициализация стратегий
        this.buildImageStrategy = new BuildImageStrategy(dockerClient, tempDirectory);

        // Заполняем карту предварительно собранных образов
        initializePrebuiltImages();

        this.mountVolumeStrategy = new MountVolumeStrategy(dockerClient, tempDirectory, prebuiltImages);

        try {
            dockerClient.pingCmd().exec();
            log.info("Docker daemon is available");
            CompletableFuture.runAsync(this::buildPrebuiltImages);
        } catch (Exception e) {
            log.error("Docker daemon is not available. Make sure Docker is running", e);
        }
    }

    private void initializePrebuiltImages() {
        for (ProgrammingLanguage language : ProgrammingLanguage.values()) {
            prebuiltImages.put(language.getValue(), "kpi-execution-" + language.getValue() + ":latest");
        }
    }

    // Построение предварительно собранных образов для всех поддерживаемых языков
    private void buildPrebuiltImages() {
        log.info("Building prebuilt Docker images for languages...");

        // Создаем образы для каждого языка
        for (Map.Entry<String, String> entry : prebuiltImages.entrySet()) {
            String language = entry.getKey();
            String imageName = entry.getValue();

            try {
                log.info("Building image for language {}: {}", language, imageName);

                // Создаем временный каталог для сборки образа
                Path tempBuildPath = Files.createTempDirectory("kpi-docker-build-");

                // Проверяем наличие Dockerfile для prebuilt образа
                Path dockerfilePath = Paths.get("src/main/resources/docker", language, "prebuilt", "Dockerfile");
                Path scriptPath = Paths.get("src/main/resources/docker", language, "prebuilt", "run-mounted.sh");

                if (!Files.exists(dockerfilePath) || !Files.exists(scriptPath)) {
                    log.warn("Skipping prebuilt image for {}: Dockerfile or run-mounted.sh not found", language);
                    continue;
                }

                // Копируем необходимые файлы
                Path destPath = Paths.get(tempBuildPath.toString(), "Dockerfile");
                Files.copy(dockerfilePath, destPath);

                Path destScriptPath = Paths.get(tempBuildPath.toString(), "run.sh");
                Files.copy(scriptPath, destScriptPath);

                // Даем права на исполнение
                new File(destScriptPath.toString()).setExecutable(true, false);

                // Собираем образ
                BuildImageResultCallback callback = new BuildImageResultCallback();

                dockerClient.buildImageCmd()
                        .withDockerfile(new File(tempBuildPath.toString(), "Dockerfile"))
                        .withPull(true)
                        .withTags(Collections.singleton(imageName))
                        .withBaseDirectory(new File(tempBuildPath.toString()))
                        .exec(callback)
                        .awaitImageId();

                log.info("Successfully built prebuilt image for {}: {}", language, imageName);

                // Очистка временного каталога
                Files.walk(tempBuildPath)
                        .sorted((p1, p2) -> -p1.compareTo(p2))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete temp build file: {}", path, e);
                            }
                        });

            } catch (Exception e) {
                log.error("Failed to build prebuilt image for {}: {}", language, e.getMessage(), e);
            }
        }

        log.info("Finished building prebuilt Docker images for all languages");
    }

    private void createTempDirectory() {
        try {
            Path path = Paths.get(tempDirectory);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created temp directory: {}", path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory", e);
        }
    }

    @Override
    public CompletableFuture<Pair<Integer, String>> executeInContainer(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    ) {
        return executeInContainer(execId, language, files, inputs, timeoutSeconds, memoryLimitMB, cpuLimit, null);
    }

    @Override
    public CompletableFuture<Pair<Integer, String>> executeInContainer(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit,
            Long assignmentId
    ) {
        int timeout = timeoutSeconds > 0 ? timeoutSeconds : defaultExecutionTimeoutSeconds;
        long memory = memoryLimitMB > 0 ? memoryLimitMB : defaultMemoryLimitMB;
        int cpu = cpuLimit > 0 ? cpuLimit : defaultCpuLimit;

        // Выбор стратегии выполнения на основе задания
        boolean useFastMode = true;

        if (assignmentId != null) {
            Optional<Assignment> assignment = assignmentRepository.findById(assignmentId);
            if (assignment.isPresent()) {
                useFastMode = assignment.get().isUseFastExecutionMode();
            }
        }

        IDockerExecutionStrategy strategy = useFastMode ? mountVolumeStrategy : buildImageStrategy;
        String strategyName = strategy.getClass().getSimpleName();
        usedStrategies.put(execId, strategyName);
        log.info("Executing code with strategy: {}", strategyName);

        return strategy.execute(execId, language, files, inputs, timeout, memory, cpu);
    }

    @Override
    public void cleanup(String execId) {
        try {
            // Получаем имя стратегии, которая использовалась для данного execId
            String strategyName = usedStrategies.remove(execId);

            if (strategyName == null) {
                log.warn("No strategy information for execution ID: {}, cleaning both strategies", execId);
                buildImageStrategy.cleanup(execId);
                mountVolumeStrategy.cleanup(execId);
                return;
            }

            // Очищаем ресурсы только для использованной стратегии
            if (strategyName.equals(BuildImageStrategy.class.getSimpleName())) {
                log.debug("Cleaning up BuildImageStrategy resources for: {}", execId);
                buildImageStrategy.cleanup(execId);
            } else if (strategyName.equals(MountVolumeStrategy.class.getSimpleName())) {
                log.debug("Cleaning up MountVolumeStrategy resources for: {}", execId);
                mountVolumeStrategy.cleanup(execId);
            } else {
                log.warn("Unknown strategy: {}, cleaning both strategies", strategyName);
                buildImageStrategy.cleanup(execId);
                mountVolumeStrategy.cleanup(execId);
            }
        } catch (Exception e) {
            log.warn("Error during cleanup for execution ID: {}", execId, e);
        }
    }
    @Override
    public void initializeImages() {
        CompletableFuture.runAsync(this::buildPrebuiltImages);
    }
}