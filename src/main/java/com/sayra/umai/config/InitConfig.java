package com.sayra.umai.config;

import com.sayra.umai.service.impl.AuthorServiceImpl;
import com.sayra.umai.service.impl.GenreServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {
    @Bean
    public CommandLineRunner fillDbWithGenres(GenreServiceImpl genreService) {
        return args -> {
            genreService.fillDbWithGenres();
        };
    }

    @Bean
    public CommandLineRunner fillDbWithKyrgyzAuthors(AuthorServiceImpl authorService) {
        return args -> {
            authorService.createKyrgyzNationalAuthor();
        };
    }
}
