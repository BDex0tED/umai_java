package com.sayra.umai.controller;

import com.sayra.umai.model.dto.JWTResponse;
import com.sayra.umai.model.dto.LoginDTO;
import com.sayra.umai.model.dto.UserDTO;
import com.sayra.umai.service.impl.UserService;
import com.sayra.umai.model.request.ChangePasswordRequest;
import com.sayra.umai.model.request.TokenRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO){
        // No try-catch! If it fails, the ExceptionHandler catches it.
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        return ResponseEntity.ok(userService.login(loginDTO, response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        userService.logout(response);
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JWTResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    @PostMapping("/google-login")
    public ResponseEntity<JWTResponse> googleLogin(@RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.googleLogin(tokenRequest, response));
    }
}