package com.sayra.umai.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class WorkInDTO {
    private String title;
    private String author;
    private String description;
    private MultipartFile filepath;

}
