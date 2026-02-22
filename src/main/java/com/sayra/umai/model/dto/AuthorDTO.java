package com.sayra.umai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private String dateOfBirth;
    private String wiki;
    private String profilePhotoUrl;
    private List<WorkDTO> works;

}
