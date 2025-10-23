package com.sayra.umai.UserPackage.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String email;
    private String username;
    private String password;
    private String profilePhotoUrl;
}
