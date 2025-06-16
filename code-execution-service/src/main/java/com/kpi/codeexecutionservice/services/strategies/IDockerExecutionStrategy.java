package com.kpi.codeexecutionservice.services.strategies;

import org.springframework.data.util.Pair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IDockerExecutionStrategy {
    CompletableFuture<Pair<Integer, String>> execute(
            String execId,
            String language,
            List<Pair<String, String>> files,
            List<String> inputs,
            int timeoutSeconds,
            long memoryLimitMB,
            int cpuLimit
    );

    void cleanup(String execId);
}