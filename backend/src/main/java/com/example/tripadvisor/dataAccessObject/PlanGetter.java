package com.example.tripadvisor.dataAccessObject;

import com.example.tripadvisor.model.Attraction;
import com.example.tripadvisor.model.Company;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import org.json.JSONObject;

@Repository
public class PlanGetter {

    private final JdbcTemplate jdbcTemplate;

    public PlanGetter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> getPlan(String country, String city) {
        String sql = "SELECT plan " +
                "FROM TripAdviseApp.TravelInfo, TripAdviseApp.DetailedPlan " +
                "WHERE country=\'" + country + "\' AND city=\'" + city + "\' " +
                "AND TripAdviseApp.TravelInfo.detailed_plan_num = TripAdviseApp.DetailedPlan.num;";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                rs.getString("plan")
        );
    }
}
