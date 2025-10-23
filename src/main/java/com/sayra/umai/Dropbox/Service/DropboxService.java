package com.sayra.umai.Dropbox.Service;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class DropboxService {

    private final DbxClientV2 dropboxClient;

    @Value("${dropbox.root.path:/umai-temp-storage}")
    private String rootPath;

    public DropboxService(DbxClientV2 dropboxClient) {
        this.dropboxClient = dropboxClient;
    }

    /**
     * Загружает файл в Dropbox и возвращает прямую ссылку на файл
     * @param file файл для загрузки
     * @param subfolder подпапка для организации файлов
     * @return прямая ссылка на файл
     * @throws Exception если произошла ошибка при загрузке
     */
    public String uploadFile(MultipartFile file, String subfolder) throws Exception {
        // Генерируем уникальное имя файла с временной меткой
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = timestamp + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + file.getOriginalFilename();
        
        String filePath = rootPath + "/" + subfolder + "/" + uniqueFileName;
        
        try (InputStream in = file.getInputStream()) {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(filePath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);

            // Создаем прямую ссылку на файл
            return createSharedLink(metadata.getPathLower());
        }
    }

    /**
     * Загружает файл с кастомным именем
     * @param file файл для загрузки
     * @param subfolder подпапка
     * @param customFileName кастомное имя файла
     * @return прямая ссылка на файл
     * @throws Exception если произошла ошибка при загрузке
     */
    public String uploadFileWithCustomName(MultipartFile file, String subfolder, String customFileName) throws Exception {
        String filePath = rootPath + "/" + subfolder + "/" + customFileName;
        
        try (InputStream in = file.getInputStream()) {
            FileMetadata metadata = dropboxClient.files()
                    .uploadBuilder(filePath)
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(in);

            return createSharedLink(metadata.getPathLower());
        }
    }

    /**
     * Создает прямую ссылку на файл в Dropbox
     * @param pathLower путь к файлу в Dropbox
     * @return прямая ссылка на файл
     * @throws DbxException если произошла ошибка при создании ссылки
     */
    private String createSharedLink(String pathLower) throws DbxException {
        try {
            // Пытаемся создать новую ссылку
            SharedLinkMetadata linkMetadata = dropboxClient.sharing()
                    .createSharedLinkWithSettings(pathLower);
            return linkMetadata.getUrl().replace("?dl=0", "?raw=1");
        } catch (DbxException e) {
            // Если ссылка уже существует, получаем существующую
            try {
                return dropboxClient.sharing()
                        .listSharedLinksBuilder()
                        .withPath(pathLower)
                        .start()
                        .getLinks()
                        .get(0)
                        .getUrl()
                        .replace("?dl=0", "?raw=1");
            } catch (Exception ex) {
                throw new DbxException("Не удалось создать или получить ссылку на файл: " + ex.getMessage());
            }
        }
    }

    /**
     * Удаляет файл из Dropbox
     * @param filePath путь к файлу в Dropbox
     * @throws DbxException если произошла ошибка при удалении
     */
    public void deleteFile(String filePath) throws DbxException {
        try {
            dropboxClient.files().deleteV2(filePath);
        } catch (DbxException e) {
            throw new DbxException("Не удалось удалить файл: " + e.getMessage());
        }
    }

    /**
     * Проверяет существование файла в Dropbox
     * @param filePath путь к файлу
     * @return true если файл существует
     */
    public boolean fileExists(String filePath) {
        try {
            dropboxClient.files().getMetadata(filePath);
            return true;
        } catch (DbxException e) {
            return false;
        }
    }

    /**
     * Получает метаданные файла
     * @param filePath путь к файлу
     * @return метаданные файла
     * @throws DbxException если произошла ошибка
     */
    public FileMetadata getFileMetadata(String filePath) throws DbxException {
        return (FileMetadata) dropboxClient.files().getMetadata(filePath);
    }

    /**
     * Очищает старые временные файлы (старше указанного количества дней)
     * @param subfolder подпапка для очистки
     * @param daysOld количество дней (файлы старше этого возраста будут удалены)
     * @return количество удаленных файлов
     */
    public int cleanOldTempFiles(String subfolder, int daysOld) {
        int deletedCount = 0;
        try {
            // Получаем список файлов в папке
            var result = dropboxClient.files().listFolder(rootPath + "/" + subfolder);
            
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
            
            for (var metadata : result.getEntries()) {
                if (metadata instanceof FileMetadata fileMetadata) {
                    // Проверяем дату создания файла
                    LocalDateTime fileDate = LocalDateTime.parse(
                        fileMetadata.getClientModified().toString().substring(0, 19)
                    );
                    
                    if (fileDate.isBefore(cutoffDate)) {
                        try {
                            dropboxClient.files().deleteV2(metadata.getPathLower());
                            deletedCount++;
                        } catch (DbxException e) {
                            // Логируем ошибку, но продолжаем очистку
                            System.err.println("Ошибка при удалении файла " + metadata.getPathLower() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (DbxException e) {
            System.err.println("Ошибка при очистке временных файлов: " + e.getMessage());
        }
        
        return deletedCount;
    }
}
