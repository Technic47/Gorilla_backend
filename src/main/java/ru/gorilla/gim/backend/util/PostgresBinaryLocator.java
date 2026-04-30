package ru.gorilla.gim.backend.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public final class PostgresBinaryLocator {

    private PostgresBinaryLocator() {
    }

    public static String resolve(String configuredPath, String binaryName) {
        if (configuredPath != null && containsPathSeparator(configuredPath)) {
            File f = new File(configuredPath);
            if (f.isFile() && f.canExecute()) {
                return f.getAbsolutePath();
            }
            log.warn("Configured {} path does not exist or is not executable: {}",
                    binaryName, configuredPath);
        }

        String pgHome = System.getenv("PGHOME");
        if (pgHome != null && !pgHome.isBlank()) {
            Path candidate = Path.of(pgHome, "bin", windowsExe(binaryName));
            if (Files.isExecutable(candidate)) {
                return candidate.toString();
            }
        }

        if (isWindows()) {
            Optional<String> found = scanWindowsInstall(binaryName);
            if (found.isPresent()) {
                return found.get();
            }
        }

        return configuredPath != null && !configuredPath.isBlank() ? configuredPath : binaryName;
    }

    private static boolean containsPathSeparator(String s) {
        return s.contains("/") || s.contains("\\");
    }

    private static String windowsExe(String name) {
        return isWindows() ? name + ".exe" : name;
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private static Optional<String> scanWindowsInstall(String binaryName) {
        return Stream.of("C:\\Program Files\\PostgreSQL", "C:\\Program Files (x86)\\PostgreSQL")
                .map(File::new)
                .filter(File::isDirectory)
                .flatMap(root -> {
                    File[] versions = root.listFiles(File::isDirectory);
                    return versions == null ? Stream.empty() : Arrays.stream(versions);
                })
                .sorted(Comparator.comparing((File f) -> versionKey(f.getName())).reversed())
                .map(versionDir -> new File(versionDir, "bin\\" + binaryName + ".exe"))
                .filter(f -> f.isFile() && f.canExecute())
                .findFirst()
                .map(File::getAbsolutePath);
    }

    private static int versionKey(String s) {
        try {
            return Integer.parseInt(s.split("\\.")[0]);
        } catch (Exception e) {
            return -1;
        }
    }
}
