package com.sayra.umai.Repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.sayra.umai.Entities.Author;

import java.util.Optional;

@Repository
public interface AuthorRepo extends CrudRepository<Author, Long> {
    Optional<Author> findByName(String name);
}
