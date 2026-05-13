package com.sayra.umai.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для работы с облачным хранилищем медиафайлов (Cloudinary).
 * Предоставляет методы для загрузки, удаления и проверки наличия файлов.
 */
public interface CloudinaryService {

    /**
     * Загружает файл в Cloudinary.
     *
     * @param file      файл для загрузки
     * @param folder    папка (например: "covers", "profiles")
     * @return публичный URL загруженного файла
     * @throws Exception при ошибке загрузки
     */
    String uploadFile(MultipartFile file, String folder) throws Exception;

    /**
     * Удаляет файл из Cloudinary по его publicId.
     *
     * @param publicId идентификатор ресурса в Cloudinary (например: "covers/abc123")
     * @throws Exception при ошибке удаления
     */
    void deleteFile(String publicId) throws Exception;

    /**
     * Проверяет, существует ли ресурс с данным publicId.
     *
     * @param publicId идентификатор ресурса
     * @return true если файл существует
     */
    boolean fileExists(String publicId);
}
