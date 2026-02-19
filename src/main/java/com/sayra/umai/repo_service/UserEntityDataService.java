package com.sayra.umai.repo_service;

import com.sayra.umai.model.entity.user.UserEntity;

import java.util.Optional;

public interface UserEntityDataService {
    UserEntity findByUsernameOrThrow(String username);
    boolean existsByRoles_NameOrThrow(String name);
    boolean existsByUsernameOrThrow(String username);
    boolean existsByEmailOrThrow(String email);
    boolean existsByGoogleId(String googleId);
    UserEntity findByGoogleIdOrThrow(String email);
    UserEntity findByEmailOrThrow(String email);
}
