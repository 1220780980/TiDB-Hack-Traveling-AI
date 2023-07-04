package com.example.tripadvisor.controller;

import com.example.tripadvisor.dataAccessObject.Fortune500Dao;
import com.example.tripadvisor.model.Company;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
public class CompanyController {

    private final Fortune500Dao fortune500Dao;

    @Autowired
    public CompanyController(Fortune500Dao fortune500Dao) {
        this.fortune500Dao = fortune500Dao;
    }

    @GetMapping("/companies")
    public List<Company> getCompanies() {
        return fortune500Dao.getCompanies();
    }


}
