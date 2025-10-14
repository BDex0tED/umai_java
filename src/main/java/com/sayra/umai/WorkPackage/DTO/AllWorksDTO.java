package com.sayra.umai.WorkPackage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllWorksDTO {
    private Long id;
    private String title;
    private String description;
    private String authorName;
    private List<GenreDTO> genres;
}
