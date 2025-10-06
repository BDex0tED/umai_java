package com.sayra.umai.DTO;

import com.sayra.umai.Entities.Author;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

@Data
public class AuthorInDTO {
    private MultipartFile photo;
    private URL wiki;
    private String name;
    private String bio;
    private String dateOfBirth;


}
