package com.sayra.umai.WorkPackage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDTO {
    private Long id;
    private String name;
    private String bio;
    private String dateOfBirth;
    private URL wiki;
    private Set<WorkInAuthorInDTO> works;

}
