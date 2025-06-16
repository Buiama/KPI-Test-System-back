package com.kpi.codeexecutionservice.services.implementations;

import com.kpi.codeexecutionservice.services.interfaces.IDockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerInitializerService {

    private final IDockerService dockerService;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDockerImages() {
        log.info("Starting Docker image initialization");
        // Инициализируем предварительно собранные образы
        try {
            dockerService.initializeImages();
            log.info("Docker image initialization completed successfully");
        } catch (Exception e) {
            log.error("Error initializing Docker images", e);
        }
    }
}