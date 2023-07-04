package com.example.tripadvisor.dataAccessObject;

import com.example.tripadvisor.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Fortune500Dao {

    private final JdbcTemplate jdbcTemplate;

    public Fortune500Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Company> getCompanies() {
        String sql = "SELECT `company_name`, `rank`, `country`, `employees_num` FROM fortune500.fortune500_2018_2022 LIMIT 5;";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Company(
                        rs.getString("company_name"),
                        rs.getInt("rank"),
                        rs.getString("country"),
                        rs.getInt("employees_num")
                )
        );
    }

}
