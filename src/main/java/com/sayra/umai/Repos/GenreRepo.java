package com.sayra.umai.Repos;

import com.sayra.umai.Entities.Genre;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepo extends CrudRepository<Genre, Integer> {
    Optional<Genre> findByGenre(String name);
}
