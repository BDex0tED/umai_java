package com.sayra.umai.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterOutDTO {
    private int chapterNumber;
    private String chapterTitle;

    private List<ChunkOutDTO> chunks;
}
