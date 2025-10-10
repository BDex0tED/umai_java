package com.sayra.umai.DTO;

import com.sayra.umai.Entities.Author;
import com.sayra.umai.Entities.Genre;
import com.sayra.umai.Other.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import java.net.URL;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkOutDTO {
    private Long workId;
    private String title;
    private String description;
    private AuthorSummaryDTO author;
    private Set<GenreDTO> genres;
    private Set<ChapterOutDTO> chapters;
    private URL coverUrl;
    private List<WorkBriefDTO> otherWorks;





}
