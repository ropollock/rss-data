package org.russpollock.rss.repository;

import com.google.gson.Gson;
import org.russpollock.rss.models.Article;
import org.russpollock.rss.models.ArticleHit;
import org.russpollock.rss.repository.queries.SearchQuery;
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

public class EsArticleDatastore implements ArticleDatastore {
    private static final Logger LOGGER = LogManager.getLogger(ElasticSearchClient.class);
    private Client client;
    private String[] searchIndicies;
    private String writeIndex;
    private final String ARTICLE_TYPE = "article";

    public EsArticleDatastore(final Client client) {
        this.client = client;
    }

    public EsArticleDatastore(final Client client,
                              final String writeIndex) {
        this.client = client;
        this.writeIndex = writeIndex;
    }

    public EsArticleDatastore(final Client client,
                              final String[] searchIndicies) {
        this.client = client;
        this.searchIndicies = searchIndicies;
    }

    public EsArticleDatastore(final Client client,
                              final String writeIndex,
                              final String[] searchIndicies) {
        this.client = client;
        this.writeIndex = writeIndex;
        this.searchIndicies = searchIndicies;
    }

    /**
     * setSearchIndicies
     *
     * Sets searchIndicies to use for search queries
     *
     * @param searchIndicies String[]
     */
    public void setSearchIndicies(final String[] searchIndicies) {
        this.searchIndicies = searchIndicies;
    }

    /**
     * setWriteIndex
     *
     * Sets write index to use for indexing actions
     *
     * @param writeIndex String
     */
    public void setWriteIndex(final String writeIndex) {
        this.writeIndex = writeIndex;
    }

    /**
     * getSearchIndicies
     *
     * Gets searchIndicies used for search queries
     *
     * @return String[]
     */
    public String[] getSearchIndicies() {
        return this.searchIndicies;
    }

    /**
     * getWriteIndex
     *
     * Gets writeIndex used for indexing actions
     *
     * @return String
     */
    public String getWriteIndex() {
        return this.writeIndex;
    }

    private IndexRequestBuilder buildArticleIndexRequest(final Article article, final String index) {
        return client.prepareIndex(index, ARTICLE_TYPE, article.URL)
                .setSource(article.serializeJSON(), XContentType.JSON);
    }

    /**
     * save
     *
     * Indexes a single Article using the writeIndex as a target.
     * Implements ArticleDatastore.save
     *
     * @param article Article
     * @throws NullPointerException
     */
    public void save(final Article article) throws NullPointerException {
        LOGGER.info(String.format("Indexing article in %s URL: %s", this.writeIndex, article.URL));
        if(this.writeIndex == null) {
            throw new NullPointerException("writeIndex must be set before indexing.");
        }
        buildArticleIndexRequest(article, this.writeIndex).get();
    }

    /**
     * saveAll
     *
     * Saves a list of Articles using the writeIndex as a target.
     * Implements ArticleDatastore.saveAll
     *
     * @param articles List of Articles
     * @throws NullPointerException
     */
    public void saveAll(final List<Article> articles) throws NullPointerException {
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

    /**
     * delete
     *
     * Removes an article from the datastore by id using writeIndex as the target.
     * Implements ArticleDatastore.delete
     *
     * @param id String
     * @throws NullPointerException
     */
    public void delete(final String id) throws NullPointerException {
        LOGGER.info(String.format("Deleting article %s", id));
        if(this.writeIndex == null) {
            throw new NullPointerException("writeIndex must be set before deleting.");
        }
        client.prepareDelete(this.writeIndex, ARTICLE_TYPE, id).get();
    }

    /**
     * search
     *
     * Performs a search against the datastore using searchIndicies as the target and a SearchQuery.
     * Returns a list of ArticleHits.
     * Implements ArticleDatastore.search
     *
     * If SearchQuery getSearchQuery() is null a match all query is used.
     * If not null than a MultiMatchQuery is used against the SearchQuery fields.
     *
     * SearchQuery.getFilters() will be applied to the post filter of the ES search.
     * If present SearchQuery.getFrom() will be used as the `from` of the ES search.
     * If present SearchQuery.getLimit() will be used as the `size` of the ES search.
     *
     * @param query SearchQuery
     * @return List<ArticleHit>
     * @throws NullPointerException
     */
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

    /**
     * search
     *
     * Performs a search against the datastore using an ES simple query string
     * and using searchIndicies as the target.
     * Returns a list of ArticleHits.
     *
     * @param queryStr String Simple Query String
     * @return List<ArticleHit>
     * @throws NullPointerException
     */
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
