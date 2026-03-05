package com.sayra.umai.service;

import com.sayra.umai.model.dto.AuthorDTO;
import com.sayra.umai.model.request.AuthorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AuthorService {
  @Transactional(readOnly = true)
  Page<AuthorDTO> getAllAuthors(Pageable pageable);


  AuthorDTO getAuthorById(Long authorId);
  AuthorDTO save(AuthorRequest authorRequest);
}
