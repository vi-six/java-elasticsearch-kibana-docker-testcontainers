package com.jek.elasticsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jek.Orc;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ElasticSearchRestConnector implements ElasticSearchRepository {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String INDEX = "jek";
    private static final String TYPE = "orcs";
    private RestHighLevelClient client;

    public ElasticSearchRestConnector(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean addToIndex(Orc orc) throws IOException {
        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE);
        indexRequest.source("name", orc.getName(),
                "strength", orc.getStrength(),
                "hp", orc.getHp());
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);
        if (index.status() == RestStatus.CREATED) {
            return true;
        } else {
            log.error("Failed to add {} to index {}. Response: {}", orc, INDEX, index);
            return false;
        }
    }

    public List<Orc> searchFromIndexByName(String queryString) {
        if (queryString == null) {
            return Collections.emptyList();
        }
        SearchRequest searchRequest = new SearchRequest(INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(queryString).field("name"));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            return Stream.of(response.getHits().getHits())
                    .map(hit -> GSON.fromJson(hit.getSourceAsString(), Orc.class))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return Collections.emptyList();
    }

    @PreDestroy
    private void closeResources() {
        try {
            this.client.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
