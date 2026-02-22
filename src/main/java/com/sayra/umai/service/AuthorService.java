package com.sayra.umai.service;

import com.sayra.umai.model.dto.AuthorDTO;
import com.sayra.umai.model.entity.work.Author;
import com.sayra.umai.model.request.AuthorRequest;

import java.util.List;

public interface AuthorService {
  List<AuthorDTO> getAllAuthors();
  AuthorDTO getAuthorById(Long authorId);
  AuthorDTO save(AuthorRequest authorRequest);
}
