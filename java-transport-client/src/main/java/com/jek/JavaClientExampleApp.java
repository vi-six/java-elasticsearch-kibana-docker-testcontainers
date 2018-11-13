package com.jek;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JavaClientExampleApp {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) throws UnknownHostException {

        Client client = getElasticsearchClient();

        storeIndexInElastic(client);

        SearchResponse response = client.prepareSearch()
                .setIndices("jek")
                .setTypes("orcs")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setPostFilter(QueryBuilders.matchQuery("name", "Thrall"))
                .execute()
                .actionGet();

        List<Orc> result = Stream.of(response.getHits().getHits())
                .map(hit -> GSON.fromJson(hit.getSourceAsString(), Orc.class))
                .collect(Collectors.toList());

        System.out.println(result.toString());

        client.close();
    }

    private static void storeIndexInElastic(Client client) {
        String s = GSON.toJson(new Orc("Garukh", 50, 100));
        String s1 = GSON.toJson(new Orc("Bokhnar", 70, 90));
        String s2 = GSON.toJson(new Orc("Thrall", 90, 120));

        client.prepareIndex("jek", "orcs").setSource(s, XContentType.JSON).get();
        client.prepareIndex("jek", "orcs").setSource(s1, XContentType.JSON).get();
        client.prepareIndex("jek", "orcs").setSource(s2, XContentType.JSON).get();
    }


    private static Client getElasticsearchClient() throws UnknownHostException {
        Settings elasticsearch = Settings.builder()
                .put("client.transport.sniff", true)
                .put("cluster.name", "elasticsearch")
                .build();

        return new PreBuiltTransportClient(elasticsearch)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
    }
}
