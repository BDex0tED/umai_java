package com.sayra.umai.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sayra.umai.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Загружает файл в Cloudinary.
     * Файл помещается в указанную папку с уникальным именем.
     *
     * @param file   файл для загрузки
     * @param folder папка (например: "covers", "profiles")
     * @return публичный HTTPS-URL загруженного файла
     * @throws Exception при ошибке загрузки
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueId = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8);

        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", uniqueId,
                        "overwrite", true,
                        "resource_type", "auto"
                )
        );

        String url = uploadResult.get("secure_url").toString();
        log.info("Uploaded file to Cloudinary: {}", url);
        return url;
    }

    /**
     * Загружает файл с кастомным publicId.
     * Удобно, когда нужен предсказуемый идентификатор (например, для аватаров пользователей).
     *
     * @param file     файл для загрузки
     * @param folder   папка
     * @param publicId кастомный идентификатор
     * @return публичный HTTPS-URL
     * @throws Exception при ошибке загрузки
     */
    public String uploadFileWithCustomId(MultipartFile file, String folder, String publicId) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", publicId,
                        "overwrite", true,
                        "resource_type", "auto"
                )
        );

        return uploadResult.get("secure_url").toString();
    }

    /**
     * Удаляет файл из Cloudinary по его publicId.
     *
     * @param publicId полный publicId вида "folder/filename" (без расширения)
     * @throws Exception при ошибке удаления
     */
    @Override
    public void deleteFile(String publicId) throws Exception {
        if (publicId == null || publicId.isBlank()) {
            log.warn("Attempted to delete file with null or empty publicId — skipping.");
            return;
        }
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        log.info("Cloudinary delete result for '{}': {}", publicId, result.get("result"));
    }

    /**
     * Проверяет существование ресурса в Cloudinary по publicId.
     *
     * @param publicId идентификатор ресурса
     * @return true если ресурс найден
     */
    @Override
    public boolean fileExists(String publicId) {
        try {
            Map result = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            return result != null && result.get("public_id") != null;
        } catch (Exception e) {
            log.debug("File not found in Cloudinary for publicId '{}': {}", publicId, e.getMessage());
            return false;
        }
    }
}
