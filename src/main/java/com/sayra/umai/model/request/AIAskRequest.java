package com.sayra.umai.model.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AIAskRequest(
        @NotEmpty(message = "query shouldn't be empty")
        String query,
        @NotEmpty(message = "book_name shouldn't be empty")
        String bookName,
        @NotNull(message = "SessionId shouldn't be null")
        Long sessionId
) {}
