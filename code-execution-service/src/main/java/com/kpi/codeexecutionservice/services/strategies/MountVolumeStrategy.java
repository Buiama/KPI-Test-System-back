package com.kpi.codeexecutionservice.services.strategies;

import com.github.dockerjava.api.DockerClient;
import com.kpi.codeexecutionservice.exceptions.ExecutionException;
import com.kpi.codeexecutionservice.services.abstracts.AbstractDockerExecutionStrategy;
import com.kpi.codeexecutionservice.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Map;

@Slf4j
public class MountVolumeStrategy extends AbstractDockerExecutionStrategy {

    private final Map<String, String> prebuiltImages;

    public MountVolumeStrategy(DockerClient dockerClient, String tempDirectory, Map<String, String> prebuiltImages) {
        super(dockerClient, tempDirectory);
        this.prebuiltImages = prebuiltImages;
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
        FileUtils.copyRunScript(executionDir, language);

        String imageId = getPrebuiltImageForLanguage(language);
        if (imageId == null) {
            throw new ExecutionException("No prebuilt image available for language: " + language);
        }

        return runContainerWithVolume(
                imageId, execId, executionDir, timeoutSeconds, memoryLimitMB, cpuLimit,
                "sh", "/app/run.sh"
        );
    }

    private String getPrebuiltImageForLanguage(String language) {
        return prebuiltImages.get(language);
    }
}
