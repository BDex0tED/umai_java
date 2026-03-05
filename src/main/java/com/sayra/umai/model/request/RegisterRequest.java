package com.sayra.umai.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is invalid")
        @Size(min = 2, max = 100)
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
        String username,

        @NotBlank(message = "Email is invalid")
        @Email
        String email,

        @NotBlank(message = "Password is invalid")
        @Size(min = 8, max = 100)
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, and one number"
        )
        String password,

        @NotBlank(message = "Confirm password is invalid")
        String confirmPassword


) {}
