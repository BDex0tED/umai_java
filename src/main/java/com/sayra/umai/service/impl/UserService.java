package com.sayra.umai.service.impl;

import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.exception.UserNotFoundException;
import com.sayra.umai.model.dto.JWTResponse;
import com.sayra.umai.model.dto.LoginDTO;
import com.sayra.umai.model.dto.UserDTO;
import com.sayra.umai.model.entity.user.Role;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.request.TokenRequest;
import com.sayra.umai.repo.RoleRepo;
import com.sayra.umai.repo.UserEntityRepo;
import com.sayra.umai.model.request.ChangePasswordRequest;
import com.sayra.umai.exception.UserAlreadyExistsException;
import com.sayra.umai.service.jwt.JWTService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final PasswordEncoder encoder;
    private final UserEntityRepo userEntityRepo;
    private final RoleRepo roleRepo;
    private final DropboxServiceImpl dropboxServiceImpl;
    private final GoogleAuthService googleAuthService;

    @Value( "${umai.app.isproduction}")
    private boolean isProduction;

    public UserEntity getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userEntityRepo.findByUsername(username).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
    }

    public UserDTO register(UserDTO userDTO){
        if(userDTO.getUsername() == null || userDTO.getPassword() == null || userDTO.getEmail() == null){
            throw new IllegalArgumentException("Invalid username/email/password");
        }
        if(userDTO.getPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if(userEntityRepo.findByUsername(userDTO.getUsername()).isPresent()){
            log.info("Username already exists: {}" , userDTO.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        if(userEntityRepo.existsByEmail(userDTO.getEmail())){
            log.info("Email already exists: {}" , userDTO.getEmail());
            throw new UserAlreadyExistsException("Email already was registered");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDTO.getUsername());
        userEntity.setPassword(encoder.encode(userDTO.getPassword()));
        userEntity.setEmail(userDTO.getEmail());

        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow(
                ()-> new ResourceNotFoundException("Role not found"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        userEntity.setRoles(roles);

        userEntityRepo.save(userEntity);

        userDTO.setPassword(null);
        return userDTO;
    }

    public JWTResponse login(LoginDTO loginDTO, HttpServletResponse response) {
        try{
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            setRefreshCookie(response, refreshToken);

            return new JWTResponse(accessToken);
        } catch (AuthenticationException e){
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    public JWTResponse googleLogin(TokenRequest tokenRequest, HttpServletResponse response){
        try {
            UserEntity userEntity = googleAuthService.authenticateWithGoogle(tokenRequest.idToken());

            String accessToken = jwtService.generateAccessToken(userEntity);

            List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), null, authorities);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            setRefreshCookie(response, refreshToken);

            return new JWTResponse(accessToken);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid token");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest){
        if(changePasswordRequest.getOldPassword() == null || changePasswordRequest.getNewPassword() == null){
            throw new IllegalArgumentException("Invalid old password or new password");
        }if(changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())){
            throw new IllegalArgumentException("Old password and new password are the same");
        }
        if(changePasswordRequest.getNewPassword().length() < 8){
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        UserEntity userEntity = getCurrentUser();
        if(!encoder.matches(changePasswordRequest.getOldPassword(), userEntity.getPassword())){
            throw new BadCredentialsException("Invalid old password");
        }
        if(encoder.matches(changePasswordRequest.getNewPassword(), userEntity.getPassword())){
            throw new IllegalArgumentException("New password must be different from old password");
        }
        userEntity.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
        userEntityRepo.save(userEntity);
    }

    public void logout(HttpServletResponse response){
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction);
        cookie.setPath("/api/users");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public JWTResponse refreshToken(HttpServletRequest request, HttpServletResponse response){
        try{
            String refreshToken = getRefreshTokenFromCookie(request);
            if(refreshToken == null){
                log.warn("Refresh token is null");
                throw new BadCredentialsException("Refresh token is missing");
            }
            String username = jwtService.extractUserName(refreshToken);
            if(username == null || jwtService.isTokenExpired(refreshToken)){
                log.warn("Refresh attempt failed for user={} from IP={}", username, request.getRemoteAddr());
                throw new BadCredentialsException("Invalid refresh token");
            }

            UserEntity userEntity = userEntityRepo.findByUsername(username).orElseThrow(
                    () -> new UserNotFoundException("User with username: " + username + " not found"));

            List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toList());

            Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity.getUsername(), null, authorities);

            String newAccessToken = jwtService.generateToken(authentication);
            String newRefreshToken = jwtService.generateRefreshToken(authentication);

            setRefreshCookie(response, newRefreshToken);

            return new JWTResponse(newAccessToken);

        } catch(Exception e){
            throw new BadCredentialsException("Invalid refresh token");
        }
    }

    private void setRefreshCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(isProduction)
                .path("/api/users")
                .sameSite(isProduction ? "Strict" : "Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public String uploadProfilePhoto(MultipartFile profilePhoto) {
        UserEntity currentUser = getCurrentUser();

        if (profilePhoto == null || profilePhoto.isEmpty()) {
            throw new IllegalArgumentException("Profile photo is required");
        }

        try {
            if (currentUser.getProfilePhotoUrl() != null && !currentUser.getProfilePhotoUrl().isEmpty()) {
                deleteProfilePhoto();
            }

            // Note: Dropbox paths typically require a leading slash
            String dropboxPath = "/profiles/" + currentUser.getUsername();
            String photoUrl = dropboxServiceImpl.uploadFile(profilePhoto, dropboxPath);

            currentUser.setProfilePhotoUrl(photoUrl);
            userEntityRepo.save(currentUser);

            return photoUrl;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке фото профиля в Dropbox: " + e.getMessage());
        }
    }

    public void deleteProfilePhoto() {
        UserEntity currentUser = getCurrentUser();

        if (currentUser.getProfilePhotoUrl() != null && !currentUser.getProfilePhotoUrl().isEmpty()) {
            try {
                // We know exactly where we saved it, no need to parse the URL!
                String filePath = "/profiles/" + currentUser.getUsername();
                dropboxServiceImpl.deleteFile(filePath);
            } catch (Exception e) {
                log.error("Ошибка при удалении фото профиля из Dropbox: {}", e.getMessage());
            }
        }

        currentUser.setProfilePhotoUrl(null);
        userEntityRepo.save(currentUser);
    }

    public UserDTO getCurrentUserInfo() {
        UserEntity currentUser = getCurrentUser();
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(currentUser.getUsername());
        userDTO.setEmail(currentUser.getEmail());
        userDTO.setProfilePhotoUrl(currentUser.getProfilePhotoUrl());
        return userDTO;
    }
}