package com.sayra.umai.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {
    private Long id;
    private int chapterNumber;
    private String chapterTitle;

    private List<ChunkResponse> chunks;
}
