package com.sayra.umai.controller;

import com.sayra.umai.model.dto.GenreDTO;
import com.sayra.umai.model.response.GenreResponse;
import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/genre")
public class GenreController {
    private final GenreService genreService;
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }
    @GetMapping()
    public ResponseEntity<List<GenreDTO>> getAllGenre(){
        return ResponseEntity.ok(this.genreService.getAllGenre());
    }

    @PostMapping()
    public ResponseEntity<GenreResponse> createGenre(@RequestParam String name){
        Genre createdGenre = genreService.createGenre(name);
        GenreResponse genreResponse = new GenreResponse();
        genreResponse.setId(createdGenre.getId());
        genreResponse.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(genreResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id){
        genreService.deleteGenre(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id){
        return ResponseEntity.ok(genreService.getGenreById(id));
    }
}
