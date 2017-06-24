package com.russpollock.rss.repository;

import com.google.gson.Gson;
import com.russpollock.rss.models.Article;
import com.russpollock.rss.models.ArticleHit;
import com.russpollock.rss.repository.queries.SearchQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.List;

public class ESArticleDatastore implements ArticleDatastore {
    private static final Logger LOGGER = LogManager.getLogger(ElasticSearchClient.class);
    private Client client;
    private String[] searchIndicies;
    private String writeIndex;
    private final String ARTICLE_TYPE = "article";

    public ESArticleDatastore(final Client client) {
        this.client = client;
    }

    public ESArticleDatastore(final Client client,
                              final String writeIndex) {
        this.client = client;
        this.writeIndex = writeIndex;
    }

    public ESArticleDatastore(final Client client,
                              final String[] searchIndicies) {
        this.client = client;
        this.searchIndicies = searchIndicies;
    }

    public ESArticleDatastore(final Client client,
                              final String writeIndex,
                              final String[] searchIndicies) {
        this.client = client;
        this.writeIndex = writeIndex;
        this.searchIndicies = searchIndicies;
    }

    public void setSearchIndicies(final String[] searchIndicies) {
        this.searchIndicies = searchIndicies;
    }

    public void setWriteIndex(final String writeIndex) {
        this.writeIndex = writeIndex;
    }

    public String[] getSearchIndicies() {
        return this.searchIndicies;
    }

    public String getWriteIndex() {
        return this.writeIndex;
    }

    private IndexRequestBuilder buildArticleIndexRequest(final Article article, final String index) {
        return client.prepareIndex(index, ARTICLE_TYPE, article.URL)
                .setSource(article.serializeJSON(), XContentType.JSON);
    }

    public void index(final Article article) throws NullPointerException {
        LOGGER.info(String.format("Indexing article in %s URL: %s", this.writeIndex, article.URL));
        if(this.writeIndex == null) {
            throw new NullPointerException("writeIndex must be set before indexing.");
        }
        buildArticleIndexRequest(article, this.writeIndex).get();
    }

    public void indexAll(final List<Article> articles) throws NullPointerException {
        LOGGER.info(String.format("Indexing %s articles to %s", articles.size(), this.writeIndex));
        if(this.writeIndex == null) {
            throw new NullPointerException("writeIndex must be set before indexing all.");
        }
        BulkRequestBuilder builder = client.prepareBulk();
        for(Article article: articles) {
            LOGGER.info(String.format("Indexing article in %s URL: %s", this.writeIndex, article.URL));
            builder.add(buildArticleIndexRequest(article, this.writeIndex));
        }

        BulkResponse response = builder.get();
        if(response.hasFailures()) {
            LOGGER.error(String.format("Failed to index all %s articles \n%s", articles.size(),
                    response.buildFailureMessage()));
        }
    }

    public void delete(final String id) throws NullPointerException {
        LOGGER.info(String.format("Deleting article %s", id));
        if(this.writeIndex == null) {
            throw new NullPointerException("writeIndex must be set before deleting.");
        }
        client.prepareDelete(this.writeIndex, ARTICLE_TYPE, id).get();
    }

    public List<ArticleHit> search(final SearchQuery query) throws NullPointerException {
        LOGGER.debug(String.format("Performing article search: %s", query.toString()));
        if(this.searchIndicies == null) {
            throw new NullPointerException("searchIndicies must be set before searching.");
        }
        // Perform search
         SearchRequestBuilder builder = client.prepareSearch(searchIndicies)
                 .setTypes(ARTICLE_TYPE)
                 .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        if(query.getSearchQuery() == null) {
            builder = builder.setQuery(QueryBuilders.matchAllQuery());
        }
        else {
            if(query.getFields() == null) {
                throw new NullPointerException("SearchQuery fields must be set to perform search term query.");
            }
            builder = builder.setQuery(new MultiMatchQueryBuilder(query.getSearchQuery(), query.getFields()));
        }

        if(query.getFilters() != null) {
            builder = builder.setPostFilter(query.getFilters());
        }

        if(query.getFrom() != null) {
            builder = builder.setFrom(query.getFrom());
        }

        if(query.getLimit() != null) {
            builder = builder.setSize(query.getLimit());
        }

        SearchResponse res = builder.execute().actionGet();

        // Transform search hits to ArticleHits
        return searchHitsToArticleHits(res.getHits().getHits());
    }

    public List<ArticleHit> search(final String queryStr) throws NullPointerException {
        LOGGER.debug(String.format("Performing article simple query string search: %s", queryStr));
        if(this.searchIndicies == null) {
            throw new NullPointerException("searchIndicies must be set before searching.");
        }
        // Perform search
        SearchResponse res = client.prepareSearch(searchIndicies)
                .setTypes(ARTICLE_TYPE)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(new SimpleQueryStringBuilder(queryStr))
                .execute()
                .actionGet();

        // Transform search hits to ArticleHits
        return searchHitsToArticleHits(res.getHits().getHits());
    }

    private List<ArticleHit> searchHitsToArticleHits(final SearchHit[] hits) {
        ArrayList<ArticleHit> articleHits = new ArrayList<>();
        for(SearchHit hit: hits) {
            LOGGER.debug(String.format("searchHitsToArticleHits hit: %s", hit.getSourceAsString()));
            ArticleHit a = new Gson().fromJson(hit.getSourceAsString(), ArticleHit.class);
            if(a != null) {
                a.searchScore = hit.getScore();
                a.setID(hit.getId());
                articleHits.add(a);
            }
        }
        return articleHits;
    }
}
