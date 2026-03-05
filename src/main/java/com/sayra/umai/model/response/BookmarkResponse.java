package com.sayra.umai.model.response;

import java.time.Instant;

public record BookmarkResponse(
    Long id,
    Long workId,
    String workTitle,
    Long chapterId,
    String chapterTitle,
    Long chunkId,
    String userNote,
    String workNote,
    Integer startOffset,
    Integer endOffset,
    Instant createdAt

) {}
