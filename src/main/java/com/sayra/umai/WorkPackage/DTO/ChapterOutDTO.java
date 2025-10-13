package com.sayra.umai.WorkPackage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterOutDTO {
    private int chapterNumber;
    private String chapterTitle;

    private Set<ChunkOutDTO> chunks;
}
