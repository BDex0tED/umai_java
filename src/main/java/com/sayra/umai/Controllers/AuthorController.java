package com.sayra.umai.Controllers;

import com.sayra.umai.DTO.AuthorDTO;
import com.sayra.umai.DTO.AuthorInDTO;
import com.sayra.umai.Entities.Author;
import com.sayra.umai.Repos.AuthorRepo;
import com.sayra.umai.Services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;


@RestController("/author")
public class AuthorController {
    private AuthorService authorService;
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }


    @GetMapping()
    public ResponseEntity<Set<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @PostMapping("/create-author")
    public ResponseEntity<Author> createAuthor(@RequestBody AuthorInDTO authorInDTO) {
        return authorService.save(authorInDTO);
    }
}
