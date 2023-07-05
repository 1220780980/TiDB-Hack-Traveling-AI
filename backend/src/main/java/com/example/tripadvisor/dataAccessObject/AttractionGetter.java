package com.example.tripadvisor.dataAccessObject;

import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class AttractionGetter {

    private final JdbcTemplate jdbcTemplate;

    public AttractionGetter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Attraction> getAttraction(String country, String city) {
        String sql = "SELECT num, name, indoor_outdoor, suggestion_time FROM TripAdviseApp.AttractionInfo " +
                "WHERE country=\'" + country + "\' AND city=\'" + city + "\';";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Attraction(
                        rs.getInt("num"),
                        rs.getString("name"),
                        rs.getInt("indoor_outdoor") != 0,
                        Duration.ofMinutes(rs.getInt("suggestion_time"))
                )
        );
    }
}
