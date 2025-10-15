package com.sayra.umai.UserPackage.Service;

import com.sayra.umai.Security.Service.JWTService;
import com.sayra.umai.UserPackage.DTO.JWTResponse;
import com.sayra.umai.UserPackage.DTO.LoginDTO;
import com.sayra.umai.UserPackage.DTO.UserDTO;
import com.sayra.umai.UserPackage.Entities.Role;
import com.sayra.umai.UserPackage.Entities.UserEntity;
import com.sayra.umai.UserPackage.Repo.RoleRepo;
import com.sayra.umai.UserPackage.Repo.UserEntityRepo;
import com.sayra.umai.WorkPackage.DTO.ChangePasswordDTO;
import com.sayra.umai.WorkPackage.Other.UserAlreadyExistsException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Principal;
import java.time.Duration;
import java.util.*;

@Service
public class UserService {
    final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final PasswordEncoder encoder;
    private final UserEntityRepo userEntityRepo;
    private final RoleRepo roleRepo;

    @Value( "${umai.app.isproduction}")
    private boolean isProduction;

    public UserService(UserEntityRepo userEntityRepo,
                       JWTService jwtService,
                       AuthenticationManager authManager,
                       PasswordEncoder encoder,
                       RoleRepo roleRepo) {
        this.userEntityRepo = userEntityRepo;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.encoder = encoder;
        this.roleRepo = roleRepo;
    }

    public UserEntity getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userEntityRepo.findByUsername(username).orElseThrow(
                ()-> new IllegalStateException("User not found"));
    }

    public UserDTO register(UserDTO userDTO){
        if(userEntityRepo.findByUsername(userDTO.getUsername()).isPresent()){
            System.out.println("Username already exists");
            throw new UserAlreadyExistsException("Username already exists");
        }
        if(userEntityRepo.existsByEmail(userDTO.getEmail())){
            System.out.println("Email already was registered");
            throw new UserAlreadyExistsException("Email already was registered");
        }
        if(userDTO.getUsername() == null || userDTO.getPassword() == null || userDTO.getEmail() == null){
            throw new IllegalArgumentException("Invalid username/email/password");
        }
        if(userDTO.getPassword().length()<8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");

        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(encoder.encode(userDTO.getPassword()));
        userEntity.setEmail(userDTO.getEmail());

        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow(
                ()-> new IllegalStateException("Role not found"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        userEntity.setRoles(roles);

        userEntityRepo.save(userEntity);

        userDTO.setPassword(null);
        return userDTO;
    }
    public JWTResponse login(LoginDTO loginDTO, HttpServletResponse response){
        try{
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                    .httpOnly(true)
                    .secure(isProduction)               // true в проде с HTTPS
                    .path("/")                  // путь, где cookie доступна
                    .sameSite(isProduction ? "Strict" : "Lax")         // либо "Lax" в зависимости от фронта
                    .maxAge(Duration.ofDays(7))
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return new JWTResponse(accessToken, null);
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid username or password");
        }

    }
    public ChangePasswordDTO changePassword(ChangePasswordDTO changePasswordDTO){
        if(changePasswordDTO.getOldPassword() == null || changePasswordDTO.getNewPassword() == null){
            throw new IllegalArgumentException("Invalid old password or new password");
        }if(changePasswordDTO.getOldPassword().equals(changePasswordDTO.getNewPassword())){
            throw new IllegalArgumentException("Old password and new password are the same");
        }
        if(changePasswordDTO.getNewPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        UserEntity userEntity = getCurrentUser();
        if(!encoder.matches(changePasswordDTO.getOldPassword(), userEntity.getPassword())){
            throw new BadCredentialsException("Invalid old password");
        }
        if(encoder.matches(changePasswordDTO.getNewPassword(), userEntity.getPassword())){
            throw new IllegalArgumentException("New password must be different from old password");
        }
        userEntity.setPassword(encoder.encode(changePasswordDTO.getNewPassword()));
        userEntityRepo.save(userEntity);
        return changePasswordDTO;
    }

    public

}
