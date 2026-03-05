package com.sayra.umai.repo_service;

import com.sayra.umai.model.entity.work.Author;

import java.util.*;

public interface AuthorDataService {
  Author findByNameOrThrow(String name);
  Author save(Author author);
//  Author update(Author author);
  void delete(Long id);
  Author findByIdOrThrow(Long id);
  Iterable<Author> findAll();
  boolean existsByName(String name);
}
