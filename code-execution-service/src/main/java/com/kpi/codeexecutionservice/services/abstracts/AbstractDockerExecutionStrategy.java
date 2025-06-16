package com.kpi.codeexecutionservice.services.abstracts;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.services.strategies.IDockerExecutionStrategy;
import com.kpi.codeexecutionservice.utils.DockerContainerUtils;
import com.kpi.codeexecutionservice.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDockerExecutionStrategy implements IDockerExecutionStrategy {
    
    protected final DockerClient dockerClient;
    protected final String tempDirectory;

    @Override
    public CompletableFuture<Pair<Integer, String>> execute(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeInternal(execId, language, files, inputs, timeoutSeconds, memoryLimitMB, cpuLimit);
            } catch (Exception e) {
                log.error("Error executing code in Docker container", e);
                throw new ExecutionException("Error executing code: " + e.getMessage(), e);
            }
        });
    }

    protected abstract Pair<Integer, String> executeInternal(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    ) throws Exception;

    protected String prepareExecutionEnvironment(String execId, List<Pair<String, String>> files, 
                                               List<String> inputs) throws Exception {
        String executionDir = FileUtils.createExecutionDirectory(tempDirectory, execId);
        FileUtils.writeAllFiles(executionDir, files);
        FileUtils.writeInputFile(executionDir, inputs);
        return executionDir;
    }

    @Override
    public void cleanup(String execId) {
        try {
            DockerContainerUtils.cleanupContainer(dockerClient, execId);
            performAdditionalCleanup(execId);
            FileUtils.cleanupDirectory(tempDirectory, execId);
        } catch (Exception e) {
            log.warn("Error during cleanup for execution ID: {}", execId, e);
        }
    }

    protected void performAdditionalCleanup(String execId) {
        // Переопределяется в дочерних классах при необходимости
    }

    protected Pair<Integer, String> runContainer(String imageId, String containerId, int timeoutSeconds,
                                                long memoryLimitMB, int cpuLimit) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        HostConfig hostConfig = DockerContainerUtils.createHostConfig(memoryLimitMB, cpuLimit);
        CreateContainerResponse container = DockerContainerUtils.createContainer(
                dockerClient, imageId, containerId, hostConfig);
        
        log.info("Created container: {}", container.getId());

        return startAndAwaitContainer(container.getId(), timeoutSeconds, startTime);
    }

    protected Pair<Integer, String> runContainerWithVolume(String imageId, String containerId, String executionDir,
                                                         int timeoutSeconds, long memoryLimitMB, int cpuLimit,
                                                         String... cmd) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        HostConfig hostConfig = DockerContainerUtils.createHostConfigWithVolume(executionDir, memoryLimitMB, cpuLimit);
        CreateContainerResponse container = DockerContainerUtils.createContainerWithWorkdir(
                dockerClient, imageId, containerId, hostConfig, "/app", cmd);
        
        log.info("Created container with mounted volume: {}", container.getId());

        return startAndAwaitContainer(container.getId(), timeoutSeconds, startTime);
    }

    protected Pair<Integer, String> startAndAwaitContainer(String containerId, int timeoutSeconds,
                                                           long startTime) throws InterruptedException {
        dockerClient.startContainerCmd(containerId).exec();
        log.info("Started container: {}", containerId);

        // DockerContainerUtils.waitForContainerCompletion уже возвращает Pair<Integer, String>
        Pair<Integer, String> executionResult = DockerContainerUtils.waitForContainerCompletion(
                dockerClient,
                containerId,
                timeoutSeconds
        );

        long executionTimeMs = System.currentTimeMillis() - startTime;
        log.info("Container {} completed with exit code: {} in {}ms",
                containerId, executionResult.getFirst(), executionTimeMs);
        return executionResult;
    }
}
