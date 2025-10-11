package com.sayra.umai.Other;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Инициализирует расширение unaccent для PostgreSQL при старте приложения.
 * Работает один раз, ручных SQL-скриптов не требуется.
 */
@Component
public class FTSBootstrap {

    private final JdbcTemplate jdbc;

    public FTSBootstrap(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    public void init() {
        jdbc.execute("CREATE EXTENSION IF NOT EXISTS unaccent");
    }
}