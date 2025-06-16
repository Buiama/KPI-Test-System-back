package com.kpi.codeexecutionservice.services.strategies;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import com.kpi.codeexecutionservice.services.abstracts.AbstractDockerExecutionStrategy;
import com.kpi.codeexecutionservice.utils.DockerContainerUtils;
import com.kpi.codeexecutionservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BuildImageStrategy extends AbstractDockerExecutionStrategy {

    public BuildImageStrategy(DockerClient dockerClient, String tempDirectory) {
        super(dockerClient, tempDirectory);
    }

    @Override
    protected Pair<Integer, String> executeInternal(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    ) throws Exception {
        String executionDir = prepareExecutionEnvironment(execId, files, inputs);
        FileUtils.copyDockerfile(language, executionDir);
        String imageId = buildImage(executionDir, execId);
        return runContainer(imageId, execId, timeoutSeconds, memoryLimitMB, cpuLimit);
    }

    @Override
    protected void performAdditionalCleanup(String execId) {
        DockerContainerUtils.cleanupImage(dockerClient, execId);
    }

    private String buildImage(String executionDir, String imageTag) {
        BuildImageResultCallback callback = new BuildImageResultCallback() {
            @Override
            public void onNext(BuildResponseItem item) {
                if (item.getStream() != null) {
                    log.debug(item.getStream());
                }
                super.onNext(item);
            }
        };

        String imageId = dockerClient.buildImageCmd()
                .withDockerfile(new File(executionDir, "Dockerfile"))
                .withPull(true)
                .withNoCache(true)
                .withTags(Collections.singleton(imageTag))
                .withBaseDirectory(new File(executionDir))
                .exec(callback)
                .awaitImageId();

        log.info("Built image: {} with ID: {}", imageTag, imageId);
        return imageId;
    }
}
