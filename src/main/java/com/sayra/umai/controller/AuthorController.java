package com.sayra.umai.controller;

import com.sayra.umai.model.dto.AuthorDTO;
import com.sayra.umai.model.request.AuthorRequest;
import com.sayra.umai.model.entity.work.Author;
import com.sayra.umai.service.AuthorService;
import com.sayra.umai.service.impl.AuthorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("/author")
public class AuthorController {
    private AuthorService authorService;
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }


    @GetMapping()
    public ResponseEntity<List<AuthorDTO>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody AuthorRequest authorRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.save(authorRequest));
    }
}
