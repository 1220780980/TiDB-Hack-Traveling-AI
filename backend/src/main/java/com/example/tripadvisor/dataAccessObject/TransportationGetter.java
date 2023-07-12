package com.example.tripadvisor.dataAccessObject;

import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.Company;
import com.example.tripadvisor.model.Transportation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
public class TransportationGetter {

    private final JdbcTemplate jdbcTemplate;

    public TransportationGetter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Transportation> getTransportation(String country, String city) {
        String sql = "SELECT attraction1, attraction2, time, detail FROM TripAdviseApp.TransportationDetail, " +
                "TripAdviseApp.AttractionInfo " +
                "WHERE AttractionInfo.num = TransportationDetail.attraction1;";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new Transportation(
                        rs.getInt("attraction1"),
                        rs.getInt("attraction2"),
                        Duration.ofMinutes(rs.getInt("time")),
                        rs.getString("detail")
                )
        );
    }
}