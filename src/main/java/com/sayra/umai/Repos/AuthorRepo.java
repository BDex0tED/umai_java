package com.sayra.umai.Repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.sayra.umai.Entities.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepo extends JpaRepository<Author, Long> {
    Optional<Author> findByName(String name);

    Optional<Author> findById(Long id);

    List<Author> findAll();
}
