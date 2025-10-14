package com.sayra.umai.UserPackage.Controllers;

import com.sayra.umai.UserPackage.DTO.JWTResponse;
import com.sayra.umai.UserPackage.DTO.LoginDTO;
import com.sayra.umai.UserPackage.DTO.UserDTO;
import com.sayra.umai.UserPackage.Service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
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
        return ResponseEntity.ok(userService.login(loginDTO, response));
    }
}
