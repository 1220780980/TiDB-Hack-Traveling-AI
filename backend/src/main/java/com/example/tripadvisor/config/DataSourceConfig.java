package com.example.tripadvisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${trip.jdbc.url}")
    private String url;

    @Value("${trip.jdbc.username}")
    private String username;

    @Value("${trip.jdbc.password}")
    private String password;

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url(url);
        dataSourceBuilder.username(username);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = null;
        try {
            DataSource dataSource = getDataSource();
            jdbcTemplate = new JdbcTemplate(dataSource);
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            // or throw a custom exception
            // throw new CustomException("Database connection failed");
        }
        System.out.println("Connection success");
        return jdbcTemplate;
    }
}
