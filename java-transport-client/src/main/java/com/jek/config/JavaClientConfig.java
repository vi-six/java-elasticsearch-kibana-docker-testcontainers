package com.jek.config;

import com.jek.elasticsearch.ElasticSearchJavaConnector;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
public class JavaClientConfig {

    @Bean
    public Client getJavaClient() throws UnknownHostException {
        Settings elasticsearch = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch")
                .build();

        return new PreBuiltTransportClient(elasticsearch)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }

    @Bean
    public ElasticSearchJavaConnector elasticSearchJavaConnector(Client javaClient) {
        return new ElasticSearchJavaConnector(javaClient);
    }
}
