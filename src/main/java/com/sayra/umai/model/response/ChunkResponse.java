package com.sayra.umai.model.response;

import com.sayra.umai.model.dto.ChunkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkResponse {
    private Long chunkId;
    private Integer chunkNumber;
    private ChunkType chunkType;
    private String text;
}
