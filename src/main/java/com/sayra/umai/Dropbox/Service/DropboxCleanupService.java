package com.sayra.umai.Dropbox.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DropboxCleanupService {

    private final DropboxService dropboxService;

    @Value("${dropbox.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    @Value("${dropbox.cleanup.days-old:7}")
    private int daysOld;

    public DropboxCleanupService(DropboxService dropboxService) {
        this.dropboxService = dropboxService;
    }

    /**
     * Автоматическая очистка временных файлов каждый день в 3:00 ночи
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanOldTempFiles() {
        if (!cleanupEnabled) {
            System.out.println("Dropbox cleanup is disabled");
            return;
        }

        try {
            System.out.println("Starting Dropbox cleanup at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Очищаем временные файлы в разных папках
            int deletedCovers = dropboxService.cleanOldTempFiles("covers", daysOld);
            int deletedProfiles = dropboxService.cleanOldTempFiles("profiles", daysOld);
            int deletedTemp = dropboxService.cleanOldTempFiles("temp", daysOld);
            
            int totalDeleted = deletedCovers + deletedProfiles + deletedTemp;
            
            System.out.println("Dropbox cleanup completed. Deleted files: " + totalDeleted);
            System.out.println("- Covers: " + deletedCovers);
            System.out.println("- Profiles: " + deletedProfiles);
            System.out.println("- Temp: " + deletedTemp);
            
        } catch (Exception e) {
            System.err.println("Error during Dropbox cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ручная очистка временных файлов
     * @param subfolder подпапка для очистки
     * @param daysOld количество дней (файлы старше этого возраста будут удалены)
     * @return количество удаленных файлов
     */
    public int manualCleanup(String subfolder, int daysOld) {
        if (!cleanupEnabled) {
            System.out.println("Dropbox cleanup is disabled");
            return 0;
        }

        try {
            System.out.println("Manual cleanup started for folder: " + subfolder + ", days old: " + daysOld);
            int deletedCount = dropboxService.cleanOldTempFiles(subfolder, daysOld);
            System.out.println("Manual cleanup completed. Deleted files: " + deletedCount);
            return deletedCount;
        } catch (Exception e) {
            System.err.println("Error during manual Dropbox cleanup: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Очистка всех временных файлов
     * @return общее количество удаленных файлов
     */
    public int cleanupAllTempFiles() {
        if (!cleanupEnabled) {
            System.out.println("Dropbox cleanup is disabled");
            return 0;
        }

        try {
            System.out.println("Full cleanup started at " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            int deletedCovers = dropboxService.cleanOldTempFiles("covers", daysOld);
            int deletedProfiles = dropboxService.cleanOldTempFiles("profiles", daysOld);
            int deletedTemp = dropboxService.cleanOldTempFiles("temp", daysOld);
            
            int totalDeleted = deletedCovers + deletedProfiles + deletedTemp;
            
            System.out.println("Full cleanup completed. Total deleted files: " + totalDeleted);
            return totalDeleted;
            
        } catch (Exception e) {
            System.err.println("Error during full Dropbox cleanup: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
