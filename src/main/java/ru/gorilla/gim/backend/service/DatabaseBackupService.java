package ru.gorilla.gim.backend.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.gorilla.gim.backend.dto.BackupFileDto;
import ru.gorilla.gim.backend.util.PostgresBinaryLocator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.gorilla.gim.backend.util.CommonUnits.DATE_FORMAT;
import static ru.gorilla.gim.backend.util.CommonUnits.DB_BACKUP_BUCKET;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseBackupService {

    private static final long PART_SIZE = 10 * 1024 * 1024;

    @Value("${spring.datasource.url}")
    private String dbUrl;
    @Value("${spring.datasource.username}")
    private String dbUsername;
    @Value("${spring.datasource.password}")
    private String dbPassword;
    @Value("${db.backup.pg-dump:pg_dump}")
    private String pgDumpPathConfig;
    @Value("${db.backup.psql:psql}")
    private String psqlPathConfig;
    @Value("${db.backup.mode:auto}")
    private String backupMode;
    @Value("${db.backup.docker-container:gorilla-postgres}")
    private String dockerContainer;

    private volatile String resolvedPgDump;
    private volatile String resolvedPsql;

    private final MinioClient minioClient;

    private boolean isDockerMode() {
        boolean containerNameSet = dockerContainer != null && !dockerContainer.isBlank();
        if ("native".equalsIgnoreCase(backupMode)) return false;
        if ("docker".equalsIgnoreCase(backupMode)) return containerNameSet;
        return containerNameSet && !isRunningInContainer();
    }

    // /.dockerenv is present in standard Docker images; cgroup check covers Kubernetes/containerd.
    private static boolean isRunningInContainer() {
        if (Files.exists(Path.of("/.dockerenv"))) return true;
        try {
            String cgroup = Files.readString(Path.of("/proc/1/cgroup"));
            return cgroup.contains("docker") || cgroup.contains("kubepods") || cgroup.contains("containerd");
        } catch (Exception e) {
            return false;
        }
    }

    private List<String> buildDumpCommand() {
        if (isDockerMode()) {
            log.info("pg_dump via docker exec on container: {}", dockerContainer);
            return List.of(
                    "docker", "exec",
                    "-e", "PGPASSWORD=" + dbPassword,
                    dockerContainer,
                    "pg_dump", "-U", dbUsername, getDatabaseName()
            );
        }
        return List.of(
                pgDumpBinary(),
                "-h", getDbHost(),
                "-p", getDbPort(),
                "-U", dbUsername,
                getDatabaseName()
        );
    }

    private List<String> buildRestoreCommand() {
        if (isDockerMode()) {
            log.info("psql via docker exec on container: {}", dockerContainer);
            return List.of(
                    "docker", "exec", "-i",
                    "-e", "PGPASSWORD=" + dbPassword,
                    dockerContainer,
                    "psql", "-U", dbUsername, "-d", getDatabaseName()
            );
        }
        return List.of(
                psqlBinary(),
                "-h", getDbHost(),
                "-p", getDbPort(),
                "-U", dbUsername,
                "-d", getDatabaseName()
        );
    }

    private synchronized String pgDumpBinary() {
        if (resolvedPgDump == null) {
            resolvedPgDump = PostgresBinaryLocator.resolve(pgDumpPathConfig, "pg_dump");
            log.info("Using pg_dump: {}", resolvedPgDump);
        }
        return resolvedPgDump;
    }

    private synchronized String psqlBinary() {
        if (resolvedPsql == null) {
            resolvedPsql = PostgresBinaryLocator.resolve(psqlPathConfig, "psql");
            log.info("Using psql: {}", resolvedPsql);
        }
        return resolvedPsql;
    }

    @Async
    public void backup() {
        log.info("Starting database backup");
        try {
            String objectName = "dump-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)) + ".sql";

            ProcessBuilder pb = new ProcessBuilder(buildDumpCommand());
            if (!isDockerMode()) {
                pb.environment().put("PGPASSWORD", dbPassword);
            }
            Process process = pb.start();

            // Drain stderr concurrently to prevent pipe-buffer deadlock
            StringBuilder stderr = new StringBuilder();
            Thread stderrDrainer = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    reader.lines().forEach(line -> stderr.append(line).append('\n'));
                } catch (Exception ignored) {
                }
            });
            stderrDrainer.setDaemon(true);
            stderrDrainer.start();

            try (InputStream inputStream = process.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(DB_BACKUP_BUCKET)
                                .object(objectName)
                                .stream(inputStream, -1L, PART_SIZE)
                                .contentType("application/sql")
                                .build()
                );
            }

            int exitCode = process.waitFor();
            stderrDrainer.join(5_000);

            if (exitCode != 0) {
                log.error("pg_dump failed (exit {}): {}", exitCode, stderr);
                throw new RuntimeException("Backup failed. See logs for details.");
            }

            log.info("Database backup finished successfully: {}", objectName);
        } catch (Exception e) {
            log.error("Critical error during backup: ", e);
        }
    }

    @Async
    public void restoreDataBase(String objectName) {
        log.info("Starting database restore from: {}", objectName);
        try {
            ProcessBuilder pb = new ProcessBuilder(buildRestoreCommand());
            if (!isDockerMode()) {
                pb.environment().put("PGPASSWORD", dbPassword);
            }
            Process process = pb.start();

            StringBuilder stderr = new StringBuilder();
            Thread stderrDrainer = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    reader.lines().forEach(line -> stderr.append(line).append('\n'));
                } catch (Exception ignored) {
                }
            });
            stderrDrainer.setDaemon(true);
            stderrDrainer.start();

            try (InputStream backupStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(DB_BACKUP_BUCKET)
                            .object(objectName)
                            .build());
                 OutputStream processStdin = process.getOutputStream()) {
                backupStream.transferTo(processStdin);
            }

            int exitCode = process.waitFor();
            stderrDrainer.join(5_000);

            if (exitCode != 0) {
                log.error("psql restore failed (exit {}): {}", exitCode, stderr);
                throw new RuntimeException("Restore failed. See logs for details.");
            }

            log.info("Database restore finished successfully from: {}", objectName);
        } catch (Exception e) {
            log.error("Critical error during restore: ", e);
            throw new RuntimeException("Restore failed", e);
        }
    }

    public List<BackupFileDto> listBackups() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            List<BackupFileDto> result = new ArrayList<>();

            for (Result<Item> r : minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(DB_BACKUP_BUCKET).build())) {
                Item item = r.get();
                String name = item.objectName();
                LocalDateTime createdAt = null;
                try {
                    String datePart = name.replace("dump-", "").replace(".sql", "");
                    createdAt = LocalDateTime.parse(datePart, formatter);
                } catch (DateTimeParseException ignored) {
                }
                result.add(new BackupFileDto(name, item.size(), createdAt));
            }

            result.sort(Comparator.comparing(BackupFileDto::getCreatedAt,
                    Comparator.nullsLast(Comparator.reverseOrder())));
            return result;
        } catch (Exception e) {
            log.error("Failed to list backups: ", e);
            throw new RuntimeException("Failed to list backups", e);
        }
    }

    @Async
    public void deleteOldBackups(int retentionDays) {
        log.info("Searching for backups older than {} day(s)", retentionDays);
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            int removed = 0;

            for (Result<Item> result : minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(DB_BACKUP_BUCKET).build())) {
                String name = result.get().objectName();
                try {
                    String datePart = name.replace("dump-", "").replace(".sql", "");
                    LocalDateTime backupTime = LocalDateTime.parse(datePart, formatter);
                    if (backupTime.isBefore(cutoff)) {
                        minioClient.removeObject(
                                RemoveObjectArgs.builder()
                                        .bucket(DB_BACKUP_BUCKET)
                                        .object(name)
                                        .build()
                        );
                        log.info("Removed old backup: {}", name);
                        removed++;
                    }
                } catch (DateTimeParseException e) {
                    log.warn("Skipping object with unrecognised name format: {}", name);
                }
            }

            log.info("Old backup cleanup finished: {} file(s) removed", removed);
        } catch (Exception e) {
            log.error("Error during old backup cleanup: ", e);
        }
    }

    // ── JDBC URL parsing ──────────────────────────────────────────────────────

    private String getDbHost() {
        // jdbc:postgresql://host:port/dbname
        String hostPort = dbUrl.replace("jdbc:postgresql://", "").split("/")[0];
        return hostPort.contains(":") ? hostPort.split(":")[0] : hostPort;
    }

    private String getDbPort() {
        String hostPort = dbUrl.replace("jdbc:postgresql://", "").split("/")[0];
        return hostPort.contains(":") ? hostPort.split(":")[1] : "5432";
    }

    private String getDatabaseName() {
        int lastSlash = dbUrl.lastIndexOf("/");
        int questionMark = dbUrl.indexOf("?", lastSlash);
        return questionMark != -1
                ? dbUrl.substring(lastSlash + 1, questionMark)
                : dbUrl.substring(lastSlash + 1);
    }
}
