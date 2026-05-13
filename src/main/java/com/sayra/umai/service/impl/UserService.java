package com.sayra.umai.service.impl;

import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.exception.UserNotFoundException;
import com.sayra.umai.exception.ValidationException;
import com.sayra.umai.model.dto.JWTResponse;
import com.sayra.umai.model.dto.LoginDTO;
import com.sayra.umai.model.dto.UserDTO;
import com.sayra.umai.model.entity.user.Role;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.model.request.RegisterRequest;
import com.sayra.umai.model.request.TokenRequest;
import com.sayra.umai.model.response.RegisterResponse;
import com.sayra.umai.repo.RoleRepo;
import com.sayra.umai.repo.UserEntityRepo;
import com.sayra.umai.model.request.ChangePasswordRequest;
import com.sayra.umai.exception.UserAlreadyExistsException;
import com.sayra.umai.security.model.UserPrincipal;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final CloudinaryServiceImpl cloudinaryServiceImpl;
    private final GoogleAuthService googleAuthService;

    @Value( "${umai.app.isproduction}")
    private boolean isProduction;

    public UserEntity getCurrentUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userEntityRepo.findByUsername(username).orElseThrow(
                ()-> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request, HttpServletResponse response){

        if(!request.password().equals(request.confirmPassword())){
            throw new ValidationException("Password do not match");
        }

        if(userEntityRepo.existsByUsername(request.username())){
            log.info("Username already exists: {}" , request.username());
            throw new UserAlreadyExistsException("Username already exists");
        }
        if(userEntityRepo.existsByEmail(request.email())){
            log.info("Email already exists: {}" , request.email());
            throw new UserAlreadyExistsException("Email already was registered");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.username());
        userEntity.setPassword(encoder.encode(request.password()));
        userEntity.setEmail(request.email());

        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow(
                ()-> new ResourceNotFoundException("Role not found"));
        List<Role> roles = new ArrayList<>();
        roles.add(userRole);
        userEntity.setRoles(roles);

        userEntityRepo.save(userEntity);

        UserPrincipal userPrincipal = new UserPrincipal(userEntity);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );

        String accessToken = jwtService.generateToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(authentication);

        setRefreshCookie(response, refreshToken);

        return new RegisterResponse(
                userEntity.getUsername(),
                userEntity.getEmail(),
                List.of(userEntity.getRoles().getFirst().getName()),
                accessToken
        );
    }

    @Transactional(readOnly = true)
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

            UserPrincipal userPrincipal = new UserPrincipal(userEntity);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
            String refreshToken = jwtService.generateRefreshToken(authentication);

            setRefreshCookie(response, refreshToken);

            return new JWTResponse(accessToken);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid token");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed", e);
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest){
        if(changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())){
            throw new IllegalArgumentException("Old password and new password are the same");
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
        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(isProduction)
                .path("/api/auth")
                .sameSite(isProduction ? "Strict" : "Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
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

    @Transactional
    public String uploadProfilePhoto(MultipartFile profilePhoto) {
        UserEntity currentUser = getCurrentUser();

        if (profilePhoto == null || profilePhoto.isEmpty()) {
            throw new IllegalArgumentException("Profile photo is required");
        }

        try {
            // Удаляем старое фото перед загрузкой нового
            if (currentUser.getProfilePhotoPublicId() != null) {
                deleteProfilePhoto();
            }

            String photoUrl = cloudinaryServiceImpl.uploadFile(profilePhoto, "profiles");

            // Извлекаем publicId из URL для последующего удаления
            // Формат Cloudinary URL: .../upload/v<ver>/<publicId>.<ext>
            String publicId = extractPublicId(photoUrl);

            currentUser.setProfilePhotoUrl(photoUrl);
            currentUser.setProfilePhotoPublicId(publicId);
            userEntityRepo.save(currentUser);

            return photoUrl;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке фото профиля в Cloudinary: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteProfilePhoto() {
        UserEntity currentUser = getCurrentUser();

        if (currentUser.getProfilePhotoPublicId() != null) {
            try {
                cloudinaryServiceImpl.deleteFile(currentUser.getProfilePhotoPublicId());
            } catch (Exception e) {
                log.error("Ошибка при удалении фото профиля из Cloudinary: {}", e.getMessage());
            }
        }

        currentUser.setProfilePhotoUrl(null);
        currentUser.setProfilePhotoPublicId(null);
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

    /**
     * Извлекает publicId из Cloudinary URL.
     * Пример URL:
     * https://res.cloudinary.com/{cloud}/image/upload/v1234567890/profiles/20250514_abc.jpg
     * Результат: profiles/20250514_abc
     */
    private String extractPublicId(String url) {
        if (url == null) return null;
        // Убираем расширение файла
        int dotIndex = url.lastIndexOf('.');
        String withoutExt = dotIndex > 0 ? url.substring(0, dotIndex) : url;
        // Ищем часть после "/upload/"
        int uploadIdx = withoutExt.indexOf("/upload/");
        if (uploadIdx < 0) return withoutExt;
        String afterUpload = withoutExt.substring(uploadIdx + "/upload/".length());
        // Если есть версия (/v1234567890/), пропускаем её
        if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
        }
        return afterUpload;
    }
}