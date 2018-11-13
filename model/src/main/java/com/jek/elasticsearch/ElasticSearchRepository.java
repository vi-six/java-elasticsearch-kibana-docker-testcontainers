package com.jek.elasticsearch;

import com.jek.Orc;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchRepository {
    boolean addToIndex(Orc orc) throws IOException;

    List<Orc> searchFromIndexByName(String queryString);
}
