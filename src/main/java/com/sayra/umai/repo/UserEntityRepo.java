package com.sayra.umai.repo;

import com.sayra.umai.model.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByRoles_Name(String name);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByGoogleId(String googleId);
    Optional<UserEntity> findByGoogleId(String googleId);

}
