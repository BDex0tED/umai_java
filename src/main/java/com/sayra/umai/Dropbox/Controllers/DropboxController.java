package com.sayra.umai.Dropbox.Controllers;

import com.sayra.umai.Dropbox.Service.DropboxCleanupService;
import com.sayra.umai.Dropbox.Service.DropboxService;
import com.sayra.umai.UserPackage.Service.UserService;
import com.sayra.umai.WorkPackage.Services.WorkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dropbox")
@CrossOrigin(origins = "*")
public class DropboxController {

    private final DropboxService dropboxService;
    private final DropboxCleanupService cleanupService;
    private final UserService userService;
    private final WorkService workService;

    public DropboxController(DropboxService dropboxService, 
                           DropboxCleanupService cleanupService,
                           UserService userService,
                           WorkService workService) {
        this.dropboxService = dropboxService;
        this.cleanupService = cleanupService;
        this.userService = userService;
        this.workService = workService;
    }

    /**
     * Загружает файл в Dropbox
     * @param file файл для загрузки
     * @param subfolder подпапка для организации файлов
     * @return URL загруженного файла
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subfolder") String subfolder) {
        
        try {
            String fileUrl = dropboxService.uploadFile(file, subfolder);
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "File uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Загружает фото профиля пользователя
     * @param profilePhoto файл фото профиля
     * @return URL загруженного фото
     */
    @PostMapping("/upload-profile-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @RequestParam("profilePhoto") MultipartFile profilePhoto) {
        
        try {
            String photoUrl = userService.uploadProfilePhoto(profilePhoto);
            Map<String, String> response = new HashMap<>();
            response.put("url", photoUrl);
            response.put("message", "Profile photo uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload profile photo: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Удаляет фото профиля пользователя
     */
    @DeleteMapping("/delete-profile-photo")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto() {
        try {
            userService.deleteProfilePhoto();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Profile photo deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete profile photo: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Загружает обложку для произведения
     * @param workId ID произведения
     * @param coverImage файл обложки
     * @return URL загруженной обложки
     */
    @PostMapping("/upload-cover/{workId}")
    public ResponseEntity<Map<String, String>> uploadCover(
            @PathVariable Long workId,
            @RequestParam("coverImage") MultipartFile coverImage) {
        
        try {
            String coverUrl = workService.uploadCover(workId, coverImage);
            Map<String, String> response = new HashMap<>();
            response.put("url", coverUrl);
            response.put("message", "Cover uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload cover: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Удаляет обложку произведения
     * @param workId ID произведения
     */
    @DeleteMapping("/delete-cover/{workId}")
    public ResponseEntity<Map<String, String>> deleteCover(@PathVariable Long workId) {
        try {
            workService.deleteCover(workId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cover deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete cover: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Ручная очистка временных файлов
     * @param subfolder подпапка для очистки
     * @param daysOld количество дней (файлы старше этого возраста будут удалены)
     * @return количество удаленных файлов
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> manualCleanup(
            @RequestParam("subfolder") String subfolder,
            @RequestParam(value = "daysOld", defaultValue = "7") int daysOld) {
        
        try {
            int deletedCount = cleanupService.manualCleanup(subfolder, daysOld);
            Map<String, Object> response = new HashMap<>();
            response.put("deletedCount", deletedCount);
            response.put("subfolder", subfolder);
            response.put("daysOld", daysOld);
            response.put("message", "Cleanup completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to cleanup files: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Очистка всех временных файлов
     * @return общее количество удаленных файлов
     */
    @PostMapping("/cleanup-all")
    public ResponseEntity<Map<String, Object>> cleanupAll() {
        try {
            int deletedCount = cleanupService.cleanupAllTempFiles();
            Map<String, Object> response = new HashMap<>();
            response.put("deletedCount", deletedCount);
            response.put("message", "All cleanup completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to cleanup all files: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Проверяет существование файла в Dropbox
     * @param filePath путь к файлу
     * @return true если файл существует
     */
    @GetMapping("/file-exists")
    public ResponseEntity<Map<String, Object>> fileExists(@RequestParam("filePath") String filePath) {
        try {
            boolean exists = dropboxService.fileExists(filePath);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("filePath", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to check file existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
