package com.sayra.umai.service.impl;

import com.sayra.umai.exception.ResourceNotFoundException;
import com.sayra.umai.model.dto.GenreDTO;
import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.model.request.GenreRequest;
import com.sayra.umai.repo.GenreRepo;
import com.sayra.umai.service.GenreService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {
    private GenreRepo genreRepo;
    public GenreServiceImpl(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }


    @Override
    @Transactional(readOnly=true)
    public List<GenreDTO> getAllGenre() {
        List<GenreDTO> allGenres = new ArrayList<>();
        for (Genre genre : genreRepo.findAll()) {
            GenreDTO genreDTO = new GenreDTO();
            genreDTO.setId(genre.getId());
            genreDTO.setName(genre.getName());
            allGenres.add(genreDTO);
        }

        return allGenres;
    }
    @Override
    @Transactional(readOnly=true)
    public GenreDTO getGenreById(Long genreId) {
      Genre genre = genreRepo.findById(genreId).orElseThrow(()->new ResourceNotFoundException("Genre with id: + " + genreId + " not found"));

      GenreDTO genreDTO = new GenreDTO();
      genreDTO.setName(genre.getName());
      genreDTO.setId(genre.getId());

      return genreDTO;

    }
    @Override
    @Transactional
    public GenreDTO createGenre(GenreRequest genreRequest) {
        Genre genre = new Genre();
        genre.setName(genreRequest.name());
        genreRepo.save(genre);
        return new GenreDTO(genre.getId(), genre.getName());
    }
    @Override
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
                "Эпос", "Роман", "Согуш", "Аңгеме", "Повесть", "Кыргыз классика", "Тарыхый роман", "Поэзия"
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
