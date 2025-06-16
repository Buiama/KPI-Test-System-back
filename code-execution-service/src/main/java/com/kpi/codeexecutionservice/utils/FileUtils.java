package com.kpi.codeexecutionservice.utils;

import com.kpi.codeexecutionservice.enums.ProgrammingLanguage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@UtilityClass
@Slf4j
public class FileUtils {

    public String createExecutionDirectory(String tempDirectory, String execId) throws IOException {
        Path executionPath = Paths.get(tempDirectory, execId);
        Files.createDirectories(executionPath);
        return executionPath.toString();
    }

    public void writeCodeToFile(String directory, String fileName, String code) throws IOException {
        Path filePath = Paths.get(directory, fileName);
        if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
            writer.write(code);
        }
    }

    public void writeInputFile(String directory, List<String> inputs) throws IOException {
        if (inputs == null || inputs.isEmpty()) {
            return;
        }
        
        String inputFilePath = directory + File.separator + "input.txt";
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(inputFilePath), StandardCharsets.UTF_8)) {
            for (String input : inputs) {
                writer.write(input + System.lineSeparator());
            }
        }
    }

    public void copyDockerfile(String language, String executionDir) throws IOException {
        Path sourcePath = Paths.get("src/main/resources/docker", language, "Dockerfile");
        Path destPath = Paths.get(executionDir, "Dockerfile");
        Files.copy(sourcePath, destPath);

        Path sourceDir = sourcePath.getParent();
        if (Files.exists(sourceDir)) {
            Files.list(sourceDir)
                    .filter(path -> !path.getFileName().toString().equals("Dockerfile"))
                    .forEach(path -> {
                        try {
                            Path targetPath = Paths.get(executionDir, path.getFileName().toString());
                            Files.copy(path, targetPath);
                        } catch (IOException e) {
                            log.warn("Failed to copy additional file: {}", path, e);
                        }
                    });
        }
    }

    public void copyRunScript(String executionDir, String language) throws IOException {
        Path sourcePath = Paths.get("src/main/resources/docker", language, "prebuilt", "run-mounted.sh");
        Path destPath = Paths.get(executionDir, "run.sh");

        if (!Files.exists(sourcePath)) {
            log.warn("Run script for language {} not found at {}", language, sourcePath);
            Files.writeString(destPath, "#!/bin/bash\nset -e\ncd /app\nls -la\nfind . -type f -executable -exec {} \\;\n");
        } else {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }

        File runScript = destPath.toFile();
        runScript.setExecutable(true, false);
    }

    public void cleanupDirectory(String tempDirectory, String execId) {
        Path executionDir = Paths.get(tempDirectory, execId);
        if (Files.exists(executionDir)) {
            try {
                Files.walk(executionDir)
                        .sorted((p1, p2) -> -p1.compareTo(p2))
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                log.warn("Failed to delete: {}", path, e);
                            }
                        });
                log.debug("Removed directory: {}", executionDir);
            } catch (IOException e) {
                log.warn("Error cleaning up directory: {}", executionDir, e);
            }
        }
    }

    public String getMainFileName(ProgrammingLanguage language) {
        return switch (language) {
            case JAVA -> "Main.java";
            case PYTHON -> "Main.py";
            case CSHARP -> "Program.cs";
            case CPP -> "main.cpp";
            case JAVASCRIPT -> "main.js";
            case TYPESCRIPT -> "main.ts";
            case KOTLIN -> "Main.kt";
            case GO -> "main.go";
            case JULIA -> "main.jl";
        };
    }

    public void writeAllFiles(String executionDir, List<Pair<String, String>> files) throws IOException {
        for (Pair<String, String> file : files) {
            writeCodeToFile(executionDir, file.getFirst(), file.getSecond());
        }
    }
}
