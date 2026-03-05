package com.sayra.umai.repo_service.impl;

import com.sayra.umai.model.entity.work.Author;
import com.sayra.umai.repo.AuthorRepo;
import com.sayra.umai.repo_service.AuthorDataService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorDataServiceImpl implements AuthorDataService {
  private final AuthorRepo authorRepo;

  public AuthorDataServiceImpl(AuthorRepo authorRepo) {
    this.authorRepo = authorRepo;
  }

  @Override
  @Transactional(readOnly = true)
  public Author findByNameOrThrow(String name) {
    return authorRepo.findByName(name).orElseThrow(() -> new EntityNotFoundException("Author with name " + name + " not found"));
  }
  @Override
  @Transactional
  public Author save(Author author) {
    return authorRepo.save(author);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean existsByName(String name){
    return authorRepo.existsByName(name);
  }

//  @Override
//  public Author update(Author author) {
//    return authorRepo.save(author);
//  }

  @Override
  public void delete(Long id) {
    if(authorRepo.existsById(id)){
      authorRepo.deleteById(id);
    }else{
      throw new EntityNotFoundException("Author with id " + id + " not found");
    }
  }

  @Override
  public Author findByIdOrThrow(Long id) {
    return authorRepo.findById(id).orElseThrow(()-> new EntityNotFoundException("Author with id " + id + " not found"));
  }

  @Override
  public Iterable<Author> findAll() {
    return authorRepo.findAll();
  }



}
