package com.jek.config;

import com.jek.elasticsearch.ElasticSearchRestConnector;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {
    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(
                                "localhost",
                                9200,
                                "http"))
        );
    }

    @Bean
    public ElasticSearchRestConnector getElasticSearchConnector(RestHighLevelClient restHighLevelClient) {
        return new ElasticSearchRestConnector(restHighLevelClient);
    }


}
