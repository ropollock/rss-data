package com.russpollock.rss.bootstrap;

import com.russpollock.rss.RSSEnvironment;
import com.russpollock.rss.repository.ElasticSearchClient;
import com.russpollock.rss.utils.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class Provision {
    private static final Logger LOGGER = LogManager.getLogger(Provision.class);
    private static final RSSEnvironment ENV = RSSEnvironment.getInstance();

    public static void main(String[] args) {
        final Client client = ElasticSearchClient.getInstance().getClient();

        // Get articles index name env
        final String dropAndCreateProp = ENV.getProvisionESDropAndCreateProp();
        final String articleWriteIndexProp = ENV.getProvisionESWriteIndexProp();
        final String articleWriteIndexName = System.getenv(articleWriteIndexProp);
        final Boolean dropAndCreate = Boolean.parseBoolean(System.getenv(dropAndCreateProp));

        if(articleWriteIndexName == null || articleWriteIndexName.equals("")) {
            LOGGER.error(String.format("You must set a provision write index in environment var %s.",
                    articleWriteIndexProp));
            client.close();
            System.exit(1);
        }

        // Load article index json
        final String articlesJson = Provision.getArticlesIndex();
        final String articleMappingJson = Provision.getArticleMapping();

        // Check if indicies already exist
        final boolean indexExists = client.admin().indices()
                .prepareExists(articleWriteIndexName).execute().actionGet().isExists();

        // If articles index exists handle dropping
        if(indexExists) {
            if(!Boolean.TRUE.equals(dropAndCreate)) {
                String err = "Articles index: %s already exists.";
                err += "If you'd like to drop and create the index, set environment var %s to true.";
                LOGGER.error(String.format(err, articleWriteIndexName, dropAndCreateProp));
                client.close();
                System.exit(1);
            } else {
                // Drop index
                LOGGER.info(String.format("Articles index: %s already exists, dropping index.",
                        articleWriteIndexName));
                boolean dropped = client.admin().indices().prepareDelete(articleWriteIndexName)
                        .execute().actionGet().isAcknowledged();
                if(!dropped) {
                    LOGGER.error(String.format("Unable to drop articles index %s", articleWriteIndexName));
                    client.close();
                    System.exit(1);
                }
            }
        }

        // Write articles index
        final boolean createdIndex = client.admin().indices().prepareCreate(articleWriteIndexName)
                .setSettings(articlesJson, XContentType.JSON).get().isShardsAcked();

        if(createdIndex) {
            LOGGER.info(String.format("Successfully provisioned index %s",
                    articleWriteIndexName));
        } else {
            LOGGER.error(String.format("Failed to provision index %s",
                    articleWriteIndexName));
            client.close();
            System.exit(1);
        }

        final boolean createdMapping = client.admin().indices().preparePutMapping(articleWriteIndexName)
                .setType("article")
                .setSource(articleMappingJson, XContentType.JSON).get().isAcknowledged();

        if(createdMapping) {
            LOGGER.info(String.format("Successfully provisioned article mapping for index %s",
                    articleWriteIndexName));
        } else {
            LOGGER.error(String.format("Failed to provision article mapping for index %s",
                    articleWriteIndexName));
            client.close();
            System.exit(1);
        }
    }

    public static String getArticlesIndex() throws NullPointerException {
        String articlesJson = null;
        try {
            final String articlesJsonPath = "repository/articles.json";
            articlesJson = File.readResource(
                    Provision.class
                            .getClassLoader().getResourceAsStream(articlesJsonPath));
            LOGGER.debug(String.format("articles.json: %s", articlesJson));
        } catch(IOException e) {
            LOGGER.error("Unable to load articles json", e);
        }

        if(articlesJson == null) {
            throw new NullPointerException("articles index json is null.");
        }

        return articlesJson;
    }

    public static String getArticleMapping() throws NullPointerException {
        String articleJson = null;
        try {
            final String articleMappingJsonPath = "repository/article.json";
            articleJson = File.readResource(
                    Provision.class.getClassLoader().getResourceAsStream(articleMappingJsonPath));
            LOGGER.debug(String.format("article.json: %s", articleJson));

        } catch(IOException e) {
            LOGGER.error("Unable to load article mapping json", e);
        }

        if(articleJson == null) {
            throw new NullPointerException("article mapping json is null.");
        }
        return articleJson;
    }
}
