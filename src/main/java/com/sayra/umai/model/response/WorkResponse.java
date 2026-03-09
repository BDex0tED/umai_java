package com.sayra.umai.model.response;

import com.sayra.umai.model.dto.GenreDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkResponse {
    private Long workId;
    private String title;
    private String description;
    private AuthorResponse author;
    private List<GenreDTO> genres;
    private List<ChapterResponse> chapters;
    private String coverUrl;
    private List<OtherWorksByAuthorResponse> otherWorks;





}
