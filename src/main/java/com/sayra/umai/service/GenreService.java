package com.sayra.umai.service;

import com.sayra.umai.model.dto.GenreDTO;
import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.model.request.GenreRequest;

import java.util.List;

public interface GenreService {
  List<GenreDTO> getAllGenre();
  GenreDTO getGenreById(Long genreId);
  GenreDTO createGenre(GenreRequest genreRequest);
  void deleteGenre(Long genreId);

}
