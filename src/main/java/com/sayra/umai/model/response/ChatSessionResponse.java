package com.sayra.umai.model.response;

import java.time.Instant;

public record ChatSessionResponse(
    Long id,
    String title,
    Instant createdAt

) {}
