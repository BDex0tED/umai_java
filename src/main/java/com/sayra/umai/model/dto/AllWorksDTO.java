package com.sayra.umai.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllWorksDTO {
    private Long id;
    private String title;
    private String description;
    private String authorName;
    private List<GenreDTO> genres;
    private String imageUrl;
}
