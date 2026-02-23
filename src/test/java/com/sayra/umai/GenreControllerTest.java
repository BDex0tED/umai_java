package com.sayra.umai;

import com.sayra.umai.controller.GenreController;
import com.sayra.umai.model.entity.work.Genre;
import com.sayra.umai.security.service.MeninUserDetailsService;
import com.sayra.umai.service.GenreService;
import com.sayra.umai.service.jwt.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GenreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private JWTService jwtService;

    @MockitoBean
    private MeninUserDetailsService meninUserDetailsService;

    @Test
    void getGenreById_ShouldReturnGenreJson_WhenGenreExists() throws Exception {
        Long genreId = 1L;
        Genre mockGenre = new Genre(genreId, "Epos", null);

        when(genreService.getGenreById(genreId)).thenReturn(mockGenre);

        mockMvc.perform(get("/genre/{id}", genreId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(genreId))
                .andExpect(jsonPath("$.name").value("Epos"));
    }
}
