package com.russpollock.rss.repository;

import java.security.InvalidParameterException;

public class ArticleDatastoreFactory {
    public static enum DATASTORE_TYPE {
        ELASTIC_SEARCH
    }

    private static final String ARTICLE_ES_SEARCH_INDICIES = "articleESSearchIndicies";
    private static final String ARTICLE_ES_WRITE_INDEX = "articleESWriteIndex";

    public static ArticleDatastore getArticleDatastore(DATASTORE_TYPE type)
            throws InvalidParameterException {
        switch (type) {
            case ELASTIC_SEARCH:
                final String searchIndicies = System.getenv(ARTICLE_ES_SEARCH_INDICIES);
                final String writeIndex = System.getenv(ARTICLE_ES_WRITE_INDEX);

                if(searchIndicies != null && writeIndex != null) {
                    return new ESArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            writeIndex,
                            searchIndicies.split("\\s*,\\s*"));
                } else if(writeIndex != null) {
                    return new ESArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            writeIndex);
                } else if(searchIndicies != null) {
                    return new ESArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            searchIndicies.split("\\s*,\\s*"));
                }
                else {
                    return new ESArticleDatastore(
                            ElasticSearchClient.getInstance().getClient());
                }
            default:
                throw new InvalidParameterException(
                        String.format("Unrecognized datastore type: %s", type.toString()));
        }
    }
}
