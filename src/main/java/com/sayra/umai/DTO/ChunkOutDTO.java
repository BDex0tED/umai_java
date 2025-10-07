package com.sayra.umai.DTO;

import com.sayra.umai.Other.ChunkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChunkOutDTO {
    private Long chunkId;
    private Integer chunkNumber;
    private ChunkType chunkType;
    private String text;
}
