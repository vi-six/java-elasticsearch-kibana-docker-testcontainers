package com.jek.controller;

import com.jek.Orc;
import com.jek.elasticsearch.ElasticSearchJavaConnector;
import com.jek.elasticsearch.ElasticSearchRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Api("java-client")
@RestController
@RequestMapping(path = "/elastic-java-client")
public class ElasticSearchJavaController {
    private ElasticSearchRepository elasticSearchRepository;

    public ElasticSearchJavaController(ElasticSearchJavaConnector elasticSearchRepository) {
        this.elasticSearchRepository = elasticSearchRepository;
    }

    @GetMapping("/orcs/name-search")
    @ApiOperation(value = "Orc name search", response = String.class)
    public List<Orc> querySearch(@RequestParam String query) throws IOException {
        return elasticSearchRepository.searchFromIndexByName(query);
    }

    @PostMapping("/orcs/add-to-horde")
    @ApiOperation(value = "Add Orc into Horde", response = String.class)
    public boolean addToIndex(@RequestBody Orc orc) throws IOException {
        return elasticSearchRepository.addToIndex(orc);
    }

}