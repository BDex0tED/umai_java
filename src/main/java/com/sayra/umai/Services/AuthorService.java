package com.sayra.umai.Services;

import com.sayra.umai.DTO.AuthorInDTO;
import com.sayra.umai.Entities.Author;
import com.sayra.umai.Repos.AuthorRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private AuthorRepo authorRepo;


    public AuthorService(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    public List<Author> getAllAuthors() {
        return authorRepo.findAll();
    }

    public ResponseEntity<Author> save(AuthorInDTO authorInDTO) {
        if(authorInDTO.getName() == null || authorInDTO.getName().equals("")){
            return ResponseEntity.badRequest().build();
        }
        if(authorRepo.findByName(authorInDTO.getName()).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        Author author = new Author();
        author.setName(authorInDTO.getName());
        author.setBio(authorInDTO.getBio());
        author.setWiki(authorInDTO.getWiki());
        author.setDate(authorInDTO.getDateOfBirth());
        author.setPhoto(authorInDTO.getPhoto());
//        author.setPhoto(authorInDTO.getPhoto()); should be in db then the url
//        author.setWorks(); need to be done
        authorRepo.save(author);
        return ResponseEntity.ok(author);


    }

}
