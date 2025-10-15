package com.sayra.umai.UserPackage.Repo;

import com.sayra.umai.UserPackage.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByRoles_Name(String name);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
