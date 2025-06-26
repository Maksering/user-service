package com.example.userservice.repository;

import com.example.userservice.DataBaseIntegrationTestInitClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class DataBaseIntegrationTest extends DataBaseIntegrationTestInitClass {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldSuccessfulConnect() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.isValid(1000)).isTrue();
        }
    }

    @Test
    void whenQuery_ShouldReturnResult() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertThat(result).isEqualTo(1);
    }
}
