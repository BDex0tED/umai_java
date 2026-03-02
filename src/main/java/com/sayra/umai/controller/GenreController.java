package com.sayra.umai.controller;

import com.sayra.umai.model.dto.GenreDTO;
import com.sayra.umai.model.request.GenreRequest;
import com.sayra.umai.model.response.GenreResponse;
import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.service.GenreService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genre")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping()
    public ResponseEntity<List<GenreDTO>> getAllGenre(){
        return ResponseEntity.ok(this.genreService.getAllGenre());
    }

    @PostMapping()
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreRequest genreRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenre(genreRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id){
        genreService.deleteGenre(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable @NotNull Long id){
        return ResponseEntity.ok(genreService.getGenreById(id));
    }
}
