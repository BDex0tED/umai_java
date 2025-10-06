package com.sayra.umai.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public class WorkUploadSchema {
    @Schema(description = "Book name", example = "Сынган кылыч")
    public String title;

    @Schema(description = "Author", example = "Төлөгөн Касымбеков")
    public String author;

    @Schema(description = "Description", example = "Описание")
    public String description;

    @Schema(type = "string", format = "binary", description = "PDF-файл")
    public MultipartFile file;
}
