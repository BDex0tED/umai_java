package com.sayra.umai.WorkPackage.Other;

import com.sayra.umai.WorkPackage.Services.GenreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig {
    @Bean
    public CommandLineRunner fillDbWithGenres(GenreService genreService) {
        return args -> {
            genreService.fillDbWithGenres();
        };
    }
}
