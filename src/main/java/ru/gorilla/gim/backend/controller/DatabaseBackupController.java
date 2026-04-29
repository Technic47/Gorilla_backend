package ru.gorilla.gim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.gorilla.gim.backend.controller.api.DatabaseBackupControllerApi;
import ru.gorilla.gim.backend.service.DatabaseBackupService;

@RestController
@RequestMapping("/db-backup")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DatabaseBackupController implements DatabaseBackupControllerApi {

    private final DatabaseBackupService databaseBackupService;

    @PostMapping("/backup")
    public ResponseEntity<Void> backup() {
        databaseBackupService.backup();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/restore")
    public ResponseEntity<Void> restore(@RequestParam String objectName) {
        databaseBackupService.restoreDataBase(objectName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/old-backups")
    public ResponseEntity<Void> deleteOldBackups(@RequestParam(defaultValue = "7") int retentionDays) {
        databaseBackupService.deleteOldBackups(retentionDays);
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeError(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Backup operation failed");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
