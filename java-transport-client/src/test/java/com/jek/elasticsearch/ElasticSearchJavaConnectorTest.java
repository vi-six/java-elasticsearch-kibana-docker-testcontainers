package com.jek.elasticsearch;

import com.jek.Orc;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testcontainers in action
 */
@Slf4j
class ElasticSearchJavaConnectorTest {

    private static final Orc ORC = new Orc("Taruk", 18, 25);
    private static final String HOST = "localhost";

    private ElasticsearchContainer container;
    private ElasticSearchJavaConnector client;

    @BeforeEach
    void setUp() throws UnknownHostException {
        container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:6.4.3");

        // Start the container. This step might take some time...
        container.start();

        //Get port to connect with client
        Integer mappedPort = container.getMappedPort(9300);

        Settings elasticsearch = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "docker-cluster")
                .build();

        client = new ElasticSearchJavaConnector(
                new PreBuiltTransportClient(elasticsearch)
                        .addTransportAddress(
                                new TransportAddress(
                                        InetAddress.getByName(HOST),
                                        mappedPort)
                        )
        );
    }

    @AfterEach
    void tearDown() {
        container.stop();
    }

    @Test
    void addToIndex() throws IOException {
        log.info("ADDING AN ORC");
        boolean result = client.addToIndex(ORC);
        assertTrue(result);
    }

    @Test
    void searchFromIndexByName() throws IOException, InterruptedException {
        log.info("ADDING AN ORC");
        client.addToIndex(ORC);

        //fixme need 650+ ms for elastic to heat up.
        log.info("TRYING TO FIND AN ORC, but I need to wait 1 sec.");
        Thread.sleep(1000);

        List<Orc> orcs = client.searchFromIndexByName(ORC.getName());
        log.info("FOUND IN A HORDE = " + orcs.toString());
        assertEquals(1, orcs.size());
    }
}