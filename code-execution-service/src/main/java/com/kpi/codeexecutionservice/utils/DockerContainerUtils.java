package com.kpi.codeexecutionservice.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@UtilityClass
@Slf4j
public class DockerContainerUtils {

    public HostConfig createHostConfig(long memoryLimitMB, int cpuLimit) {
        return HostConfig.newHostConfig()
                .withMemory(memoryLimitMB * 1024 * 1024)
                .withMemorySwap(memoryLimitMB * 1024 * 1024)
                .withCpuCount((long) cpuLimit)
                .withNetworkMode("none")
                .withPrivileged(false)
                .withSecurityOpts(List.of("no-new-privileges:true"));
    }

    public HostConfig createHostConfigWithVolume(String executionDir, long memoryLimitMB, int cpuLimit) {
        Volume volume = new Volume("/app");
        Bind bind = new Bind(executionDir, volume, AccessMode.rw);
        
        return createHostConfig(memoryLimitMB, cpuLimit)
                .withBinds(bind);
    }

    public Map<String, String> createLabels(String executionId) {
        Map<String, String> labels = new HashMap<>();
        labels.put("execution-id", executionId);
        return labels;
    }

    public CreateContainerResponse createContainer(DockerClient dockerClient, String imageId, String containerId, 
                                                 HostConfig hostConfig, String... cmd) {
        CreateContainerCmd builder = dockerClient.createContainerCmd(imageId)
                .withHostConfig(hostConfig)
                .withName(containerId)
                .withLabels(createLabels(containerId))
                .withAttachStdin(false)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .withStdinOpen(false);

        if (cmd.length > 0) {
            builder.withCmd(cmd);
        }

        return builder.exec();
    }

    public CreateContainerResponse createContainerWithWorkdir(DockerClient dockerClient, String imageId, 
                                                            String containerId, HostConfig hostConfig, 
                                                            String workdir, String... cmd) {
        CreateContainerCmd builder = dockerClient.createContainerCmd(imageId)
                .withHostConfig(hostConfig)
                .withName(containerId)
                .withLabels(createLabels(containerId))
                .withAttachStdin(false)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(false)
                .withStdinOpen(false)
                .withWorkingDir(workdir);

        if (cmd.length > 0) {
            builder.withCmd(cmd);
        }

        return builder.exec();
    }

    public Pair<Integer, String> waitForContainerCompletion(DockerClient dockerClient, String containerId, 
                                                           int timeoutSeconds) throws InterruptedException {
        final StringBuilder logs = new StringBuilder();
        final StringBuilder errors = new StringBuilder();

        ResultCallback<Frame> logCallback = createLogCallback(logs, errors);

        dockerClient.logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .withFollowStream(true)
                .exec(logCallback);

        boolean completed = dockerClient.waitContainerCmd(containerId)
                .exec(new ResultCallback.Adapter<>())
                .awaitCompletion(timeoutSeconds, TimeUnit.SECONDS);

        if (!completed) {
            log.warn("Execution timed out after {} seconds, killing container: {}", timeoutSeconds, containerId);
            dockerClient.killContainerCmd(containerId).exec();
            return Pair.of(-1, "Execution timed out after " + timeoutSeconds + " seconds");
        }

        int exitCode = dockerClient.inspectContainerCmd(containerId)
                .exec()
                .getState()
                .getExitCode();

        String output = logs.toString();
        if (!errors.isEmpty()) {
            output += "\nERRORS:\n" + errors;
        }

        return Pair.of(exitCode, output);
    }

    private ResultCallback<Frame> createLogCallback(StringBuilder logs, StringBuilder errors) {
        return new ResultCallback<>() {
            @Override
            public void onStart(Closeable closeable) {}

            @Override
            public void onNext(Frame frame) {
                String payload = new String(frame.getPayload(), StandardCharsets.UTF_8);
                if (frame.getStreamType() == StreamType.STDOUT) {
                    logs.append(payload);
                } else if (frame.getStreamType() == StreamType.STDERR) {
                    errors.append(payload);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("Error collecting logs", throwable);
                errors.append("Error collecting logs: ").append(throwable.getMessage());
            }

            @Override
            public void onComplete() {}

            @Override
            public void close() {}
        };
    }

    public void cleanupContainer(DockerClient dockerClient, String containerId) {
        try {
            boolean containerExists = dockerClient.listContainersCmd()
                    .withShowAll(true)
                    .withNameFilter(Collections.singletonList(containerId))
                    .exec()
                    .stream()
                    .anyMatch(container -> container.getNames()[0].substring(1).equals(containerId));

            if (containerExists) {
                dockerClient.removeContainerCmd(containerId)
                        .withForce(true)
                        .withRemoveVolumes(true)
                        .exec();
                log.debug("Removed container: {}", containerId);
            }
        } catch (Exception e) {
            log.warn("Error removing container: {}", containerId, e);
        }
    }

    public void cleanupImage(DockerClient dockerClient, String imageId) {
        try {
            boolean imageExists = dockerClient.listImagesCmd()
                    .withImageNameFilter(imageId)
                    .exec()
                    .stream()
                    .anyMatch(image -> Arrays.asList(image.getRepoTags()).contains(imageId + ":latest"));

            if (imageExists) {
                dockerClient.removeImageCmd(imageId)
                        .withForce(true)
                        .exec();
                log.debug("Removed image: {}", imageId);
            }
        } catch (Exception e) {
            log.warn("Error removing image: {}", imageId, e);
        }
    }
}
