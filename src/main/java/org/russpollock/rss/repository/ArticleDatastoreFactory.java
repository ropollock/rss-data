package org.russpollock.rss.repository;

import org.russpollock.rss.RssEnvironment;

import java.security.InvalidParameterException;

public class ArticleDatastoreFactory {
    public static enum DATASTORE_TYPE {
        ELASTIC_SEARCH
    }

    private static final RssEnvironment ENV = RssEnvironment.getInstance();

    public static ArticleDatastore getArticleDatastore(DATASTORE_TYPE type)
            throws InvalidParameterException {
        switch (type) {
            case ELASTIC_SEARCH:
                final String searchIndicies = ENV.getEnv(ENV.getESArticleSearchIndiciesProp());
                final String writeIndex = ENV.getEnv(ENV.getESArticleWriteIndexProp());

                if(searchIndicies != null && writeIndex != null) {
                    return new esArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            writeIndex,
                            searchIndicies.split("\\s*,\\s*"));
                } else if(writeIndex != null) {
                    return new esArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            writeIndex);
                } else if(searchIndicies != null) {
                    return new esArticleDatastore(
                            ElasticSearchClient.getInstance().getClient(),
                            searchIndicies.split("\\s*,\\s*"));
                }
                else {
                    return new esArticleDatastore(
                            ElasticSearchClient.getInstance().getClient());
                }
            default:
                throw new InvalidParameterException(
                        String.format("Unrecognized datastore type: %s", type.toString()));
        }
    }
}
