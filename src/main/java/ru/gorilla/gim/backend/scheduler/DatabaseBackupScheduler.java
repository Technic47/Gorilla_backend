package ru.gorilla.gim.backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.gorilla.gim.backend.service.DatabaseBackupService;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseBackupScheduler {

    private final DatabaseBackupService databaseBackupService;

    @Value("${db.backup.retention-days:7}")
    private int retentionDays;

    @Scheduled(cron = "${db.backup.cron}")
    public void backup() {
        databaseBackupService.backup();
    }

    @Scheduled(cron = "${db.backup.cleanup.cron}")
    public void deleteOldBackups() {
        databaseBackupService.deleteOldBackups(retentionDays);
    }
}
