package com.sayra.umai.Controllers;

import com.sayra.umai.DTO.GenreOutDTO;
import com.sayra.umai.Entities.Genre;
import com.sayra.umai.Services.GenreService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController()
@RequestMapping("/genre")
//add a method which will run and fill in the genres in the db
public class GenreController {
    private GenreService genreService;
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }
    @GetMapping()
    public ResponseEntity<List<Genre>> getAllGenre(){
        return ResponseEntity.ok(this.genreService.getAllGenre());
    }

    @PostMapping()
    public ResponseEntity<GenreOutDTO> createGenre(@RequestParam String name){
        Genre createdGenre = genreService.createGenre(name);
        GenreOutDTO genreOutDTO = new GenreOutDTO();
        genreOutDTO.setId(createdGenre.getId());
        genreOutDTO.setName(name);
        return ResponseEntity.ok(genreOutDTO);
    }
}
