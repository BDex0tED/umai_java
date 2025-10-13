package com.sayra.umai.WorkPackage.DTO;

import com.sayra.umai.WorkPackage.Other.ChunkType;
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
