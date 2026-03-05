package com.sayra.umai.controller;

import com.sayra.umai.model.dto.JWTResponse;
import com.sayra.umai.model.dto.LoginDTO;
import com.sayra.umai.model.dto.UserDTO;
import com.sayra.umai.model.request.RegisterRequest;
import com.sayra.umai.model.response.RegisterResponse;
import com.sayra.umai.service.impl.UserService;
import com.sayra.umai.model.request.ChangePasswordRequest;
import com.sayra.umai.model.request.TokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest, HttpServletResponse response){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registerRequest, response));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletResponse response){
        return ResponseEntity.ok(userService.login(loginDTO, response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest){
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        userService.logout(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JWTResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    @PostMapping("/google-login")
    public ResponseEntity<JWTResponse> googleLogin(@RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.googleLogin(tokenRequest, response));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUserInfo());
    }

    @PostMapping(value = "/profile-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        return ResponseEntity.ok(userService.uploadProfilePhoto(photo));
    }

    @DeleteMapping("/profile-photo")
    public ResponseEntity<Void> deleteProfilePhoto() {
        userService.deleteProfilePhoto();
        return ResponseEntity.noContent().build();
    }
}
