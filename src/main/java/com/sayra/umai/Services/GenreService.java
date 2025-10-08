package com.sayra.umai.Services;

import com.sayra.umai.DTO.GenreDTO;
import com.sayra.umai.DTO.GenreOutDTO;
import com.sayra.umai.Entities.Genre;
import com.sayra.umai.Repos.GenreRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private GenreRepo genreRepo;
    public GenreService(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }
    public Set<GenreDTO> getAllGenre() {
        Set<GenreDTO> allGenres = new HashSet<>();
        for (Genre genre : genreRepo.findAll()) {
            GenreDTO genreDTO = new GenreDTO();
            genreDTO.setId(genre.getId());
            genreDTO.setName(genre.getName());
            allGenres.add(genreDTO);
        }

        return allGenres;
    }
    public Genre createGenre(String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);
        genreRepo.save(genre);
        return genre;
    }

    @Transactional
    public void deleteGenre(Long genreId) {
        if(!genreRepo.existsById(genreId)){
            throw new EntityNotFoundException("Genre with id: "+genreId+" not found");
        }
        genreRepo.deleteById(genreId);
    }

    @Transactional
    public void fillDbWithGenres(){
        Set<String> genreNamesToAdd = new HashSet<>(Arrays.asList(
                "Эпос", "Роман", "Согуш", "Аңгеме", "Повесть", "Кыргыз классика"
        ));
        Set<String> existingGenres = genreRepo.findAll().stream().map(Genre::getName).collect(Collectors.toSet());
        genreNamesToAdd.removeAll(existingGenres);

        genreNamesToAdd.stream().map(name->{
            Genre genre = new Genre();
            genre.setName(name);
            return genre;
        }).forEach(genreRepo::save);
        System.out.println("Database was filled with these genres: " +  genreNamesToAdd);
    }
}
