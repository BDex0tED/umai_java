package com.sayra.umai.model.response;

import com.sayra.umai.model.entity.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Builder
public record RegisterResponse(
        String username,
        String email,
        List<String> roles,
        String accessToken

) {}
