package com.sayra.umai.controller;

import com.sayra.umai.service.impl.CloudinaryServiceImpl;
import com.sayra.umai.service.impl.UserService;
import com.sayra.umai.service.impl.WorkServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
@CrossOrigin(origins = "*")
public class MediaController {

    private final CloudinaryServiceImpl cloudinaryServiceImpl;
    private final UserService userService;
    private final WorkServiceImpl workServiceImpl;

    public MediaController(CloudinaryServiceImpl cloudinaryServiceImpl,
                           UserService userService,
                           WorkServiceImpl workServiceImpl) {
        this.cloudinaryServiceImpl = cloudinaryServiceImpl;
        this.userService = userService;
        this.workServiceImpl = workServiceImpl;
    }

    /**
     * Загружает файл в Cloudinary
     * @param file файл для загрузки
     * @param folder папка для организации файлов (covers, profiles и т.д.)
     * @return URL загруженного файла
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folder") String folder) {

        try {
            String fileUrl = cloudinaryServiceImpl.uploadFile(file, folder);
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
            String coverUrl = workServiceImpl.uploadCover(workId, coverImage);
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
            workServiceImpl.deleteCover(workId);
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
     * Удаляет файл из Cloudinary по его publicId
     * @param publicId идентификатор ресурса в Cloudinary
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteFile(@RequestParam("publicId") String publicId) {
        try {
            cloudinaryServiceImpl.deleteFile(publicId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete file: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Проверяет существование файла в Cloudinary
     * @param publicId идентификатор ресурса
     * @return true если файл существует
     */
    @GetMapping("/file-exists")
    public ResponseEntity<Map<String, Object>> fileExists(@RequestParam("publicId") String publicId) {
        try {
            boolean exists = cloudinaryServiceImpl.fileExists(publicId);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            response.put("publicId", publicId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to check file existence: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
