package com.jek.elasticsearch;

import com.jek.Orc;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testcontainers in action
 */
@Slf4j
class ElasticSearchRestConnectorTest {

    private static final Orc ORC = new Orc("Taruk", 18, 25);
    private static final String HTTP = "http";
    private static final String HOST = "localhost";

    private ElasticsearchContainer container;
    private ElasticSearchRestConnector client;

    @BeforeEach
    void setUp() {
        container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:6.4.3");

        // Start the container. This step might take some time...
        container.start();

        //Get port to connect with client
        Integer mappedPort = container.getMappedPort(9200);

        client = new ElasticSearchRestConnector(new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(
                                HOST,
                                mappedPort,
                                HTTP))));
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    @Test
    void addToIndex() throws IOException {
        log.info("ADDING AN ORC");
        boolean t = client.addToIndex(ORC);
        assertTrue(t);

    }

    @Test
    void searchFromIndexByName() throws InterruptedException, IOException {
        log.info("ADDING AN ORC");
        client.addToIndex(ORC);

        //fixme need 650+ ms for elastic to heat up.
        log.info("TRYING TO FIND AN ORC, but I need to wait 1 sec");
        Thread.sleep(1000);

        List<Orc> orcs = client.searchFromIndexByName(ORC.getName());
        log.info("FOUND IN A HORDE = " + orcs.toString());
        assertEquals(1, orcs.size());
    }
}