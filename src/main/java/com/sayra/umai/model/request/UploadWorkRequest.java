package com.sayra.umai.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UploadWorkRequest(
        @NotBlank(message = "Title can't be empty or null")
        String title,
        @NotNull(message = "AuthorId can't be empty or null")
        Long authorId,
        @NotEmpty(message = "Genres can't be empty or null")
        Set<Long> genresId,
        @NotBlank(message = "Description can't be empty or null")
        String description
) {}
