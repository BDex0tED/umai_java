package com.sayra.umai.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is invalid")
    @Size(min = 8)
    private String oldPassword;
    @NotBlank(message = "New password is invalid")
    @Size(min = 8)
    private String newPassword;
}
