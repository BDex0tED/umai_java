package com.sayra.umai.Services;

import com.sayra.umai.Entities.Genre;
import com.sayra.umai.Repos.GenreRepo;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class GenreService {
    private GenreRepo genreRepo;
    public GenreService(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }
    public List<Genre> getAllGenre() {
        return genreRepo.findAll();
    }
    public Genre createGenre(String genreName) {
        Genre genre = new Genre();
        genre.setName(genreName);
        genreRepo.save(genre);
        return genre;
    }
}
