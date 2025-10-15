package com.sayra.umai.WorkPackage.Controllers;

import com.sayra.umai.WorkPackage.DTO.GenreDTO;
import com.sayra.umai.WorkPackage.DTO.GenreOutDTO;
import com.sayra.umai.WorkPackage.Entities.Genre;
import com.sayra.umai.WorkPackage.Services.GenreService;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController()
@RequestMapping("/genre")
public class GenreController {
    private final GenreService genreService;
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }
    @GetMapping()
    public ResponseEntity<Set<GenreDTO>> getAllGenre(){
        return ResponseEntity.ok(this.genreService.getAllGenre());
    }

    @PostMapping()
    public ResponseEntity<GenreOutDTO> createGenre(@RequestParam String name){
        Genre createdGenre = genreService.createGenre(name);
        GenreOutDTO genreOutDTO = new GenreOutDTO();
        genreOutDTO.setId(createdGenre.getId());
        genreOutDTO.setName(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(genreOutDTO);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id){
        genreService.deleteGenre(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
