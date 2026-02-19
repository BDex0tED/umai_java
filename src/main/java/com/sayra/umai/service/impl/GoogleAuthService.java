package com.sayra.umai.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.sayra.umai.exception.UserNotFoundException;
import com.sayra.umai.model.entity.user.AuthProvider;
import com.sayra.umai.model.entity.user.Role;
import com.sayra.umai.model.entity.user.UserEntity;
import com.sayra.umai.repo.RoleRepo;
import com.sayra.umai.repo.UserEntityRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    @Value("${google.client-id}")
    private String clientId;

    private final UserEntityRepo userEntityRepo;
    private final RoleRepo roleRepo;

    private GoogleIdTokenVerifier verifier;

    @PostConstruct
    public void init() {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public UserEntity authenticateWithGoogle(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Invalid ID token.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String googleId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Optional<UserEntity> userByGoogleOpt = userEntityRepo.findByGoogleId(googleId);
            if (userByGoogleOpt.isPresent()) {
                log.info("User logged in with google id: {}", googleId);
                return userByGoogleOpt.get();
            }

            Optional<UserEntity> userByEmailOpt = userEntityRepo.findByEmail(email);
            if (userByEmailOpt.isPresent()) {
                UserEntity existingUser = userByEmailOpt.get();
                existingUser.setGoogleId(googleId);
                userEntityRepo.save(existingUser);
                log.info("Linked Google ID to existing email: {}", email);
                return existingUser;
            }

            Role roleUser = roleRepo.findByName("ROLE_USER")
                    .orElseThrow(() -> new UserNotFoundException("Default role not found in DB"));

            UserEntity newUser = new UserEntity(name, email, List.of(roleUser), AuthProvider.GOOGLE);

            newUser.setGoogleId(googleId);

            userEntityRepo.save(newUser);
            log.info("New User created via Google with email: {}", email);

            return newUser;

        } catch (Exception e) {
            log.error("Token verification failed", e);
            throw new RuntimeException("Token verification failed", e);
        }
    }
}