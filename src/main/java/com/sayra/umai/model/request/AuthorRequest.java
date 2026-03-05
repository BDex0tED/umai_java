package com.sayra.umai.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
@Data
public class AuthorRequest {
    private MultipartFile photo;
    private String wiki;
    @NotBlank(message = "Name can't be null or empty")
    @Size(min = 2, max = 255)
    private String name;
    @NotBlank(message = "Biography can't be null or empty")
    @Size(max = 1024)
    private String bio;
    @NotBlank(message = "Birthdate can't be null or empty")
    private String dateOfBirth;


    private List<Long> workIds;


}
