package com.sayra.umai.controller;

import com.sayra.umai.model.dto.JWTResponse;
import com.sayra.umai.model.dto.LoginDTO;
import com.sayra.umai.model.dto.UserDTO;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.service.impl.GoogleAuthService;
import com.sayra.umai.service.impl.UserService;
import com.sayra.umai.model.request.ChangePasswordRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import com.sayra.umai.model.request.TokenRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final GoogleAuthService googleAuthService;
    public UserController(UserService userService,GoogleAuthService googleAuthService) {
        this.userService = userService;
        this.googleAuthService = googleAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDTO));
        } catch (BadCredentialsException badCredentialsException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response){
        try {
            return ResponseEntity.ok(userService.login(loginDTO, response));
        }catch (BadCredentialsException badCredentialsException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        try {
            userService.changePassword(changePasswordRequest);
            return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        userService.logout(response);
        return ResponseEntity.ok("Logout successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JWTResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) throws BadCredentialsException, Exception{
        return ResponseEntity.ok(userService.refreshToken(request, response));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody TokenRequest tokenRequest, HttpServletResponse response) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.googleLogin(tokenRequest, response));
        } catch(BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }

}
