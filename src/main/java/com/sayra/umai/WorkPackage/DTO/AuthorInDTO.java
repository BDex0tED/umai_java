package com.sayra.umai.WorkPackage.DTO;

import lombok.Data;

import java.net.URL;

@Data
public class AuthorInDTO {
    private URL photo;
    private URL wiki;
    private String name;
    private String bio;
    private String dateOfBirth;


}
