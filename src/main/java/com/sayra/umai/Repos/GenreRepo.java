package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByName(String name);
    Optional<Genre> findById(Long id);
}
