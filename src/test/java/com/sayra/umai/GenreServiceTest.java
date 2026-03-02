package com.sayra.umai;

import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.repo.GenreRepo;
import com.sayra.umai.service.GenreService;
import com.sayra.umai.service.impl.GenreServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepo genreRepo;

    @InjectMocks
    private GenreServiceImpl genreService;

//    @Test
//    void getGenreById_ShouldReturnGenre_WhenGenreExists() {
//        Long genreId = 1L;
//        Genre genre = new Genre(1L, "Epos", null);
//
//        when(genreRepo.findById(genreId)).thenReturn(java.util.Optional.of(genre));
//
//        Genre result = genreService.getGenreById(genreId);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getId()).isEqualTo(genreId);
//        assertThat(result.getName()).isEqualTo(genre.getName());
//
//        verify(genreRepo, times(1)).findById(genreId);
//    }

    @Test
    void getGenreById_ShouldThrowException_WhenGenreDoesNotExist() {
        Long genreId = 1L;
        when(genreRepo.findById(genreId)).thenReturn(java.util.Optional.empty());
        assertThatThrownBy(() -> genreService.getGenreById(genreId)).isInstanceOf(EntityNotFoundException.class);
        verify(genreRepo, times(1)).findById(genreId);
    }

    @Test
    void deleteGenre_ShouldThrowException_WhenGenreDoesNotExist() {
        Long genreId = 1L;

        when(genreRepo.existsById(genreId)).thenReturn(false);

        assertThatThrownBy(() -> genreService.deleteGenre(genreId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(genreRepo, times(1)).existsById(genreId);

        verify(genreRepo, never()).deleteById(anyLong());
    }
}
