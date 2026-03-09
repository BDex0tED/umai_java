package com.sayra.umai.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkRequest {
    private Long id;
    private Long workId;
    private Long userId;
    private Long chapterId;
    private Long chunkId;
    private String userNote;
    private String workNote;
    private Integer startOffset;
    private Integer endOffset;

}
