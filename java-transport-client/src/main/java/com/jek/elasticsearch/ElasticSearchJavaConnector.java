package com.jek.elasticsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jek.Orc;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ElasticSearchJavaConnector implements ElasticSearchRepository {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String INDEX = "jek";
    private static final String TYPE = "orcs";

    private Client client;

    public ElasticSearchJavaConnector(Client client) {
        this.client = client;
    }

    @Override
    public boolean addToIndex(Orc orc) throws IOException {
        IndexResponse index = client.prepareIndex(INDEX, TYPE)
                .setSource(GSON.toJson(orc), XContentType.JSON)
                .get();
        if (index.status() == RestStatus.CREATED) {
            return true;
        } else {
            log.error("Failed to add {} to index {}. Response: {}", orc, INDEX, index);
            return false;
        }
    }

    @Override
    public List<Orc> searchFromIndexByName(String queryString) {
        if (queryString == null) {
            return Collections.emptyList();
        }

        SearchResponse response = client.prepareSearch()
                .setIndices(INDEX)
                .setTypes(TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setPostFilter(QueryBuilders.matchQuery("name", queryString))
                .execute()
                .actionGet();

        return Stream.of(response.getHits().getHits())
                .map(hit -> GSON.fromJson(hit.getSourceAsString(), Orc.class))
                .collect(Collectors.toList());
    }

    @PreDestroy
    private void closeResources() {
        this.client.close();
    }
}
