package com.kpi.codeexecutionservice.services.interfaces;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDockerService {
    CompletableFuture<Pair<Integer, String>> executeInContainer(
            String execId,
            String language,
            List<Pair<String, String>> files, // filename, content
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    );

    CompletableFuture<Pair<Integer, String>> executeInContainer(
            String execId,
            String language,
            List<Pair<String, String>> files, // filename, content
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit,
            Long assignmentId
    );

    void initializeImages();

    void cleanup(String execId);
}