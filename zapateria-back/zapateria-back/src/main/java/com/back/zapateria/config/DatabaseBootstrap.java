package com.back.zapateria.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseBootstrap implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseBootstrap(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("UPDATE purchase SET status = 'REGISTRADO' WHERE status IS NULL");
        } catch (Exception ignored) {
        }

        try {
            jdbcTemplate.execute("SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users))");
        } catch (Exception ignored) {
        }
    }
}