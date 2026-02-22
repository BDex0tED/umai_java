package com.sayra.umai.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
@Data
public class AuthorRequest {
    private MultipartFile photo;
    private String wiki;
    private String name;
    private String bio;
    private String dateOfBirth;

    private List<Long> workIds;


}
