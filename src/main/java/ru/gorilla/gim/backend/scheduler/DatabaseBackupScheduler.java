package ru.gorilla.gim.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.gorilla.gim.backend.service.DatabaseBackupService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseBackupScheduler {

    private final DatabaseBackupService databaseBackupService;

    @Scheduled(cron = "${db.backup.cron}")
    public void backup() {
        databaseBackupService.backup();
    }
}
