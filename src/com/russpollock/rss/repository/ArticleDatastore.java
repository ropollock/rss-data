package com.russpollock.rss.repository;

import com.russpollock.rss.models.Article;
import com.russpollock.rss.models.ArticleHit;
import com.russpollock.rss.repository.queries.SearchQuery;

import java.util.List;

public interface ArticleDatastore {
    /**
     * search
     *
     * Searches article datastore using a SearchQuery
     *
     * @param query SearchQuery
     * @return List<ArticleHit>
     */
    List<ArticleHit> search(SearchQuery query);

    /**
     * indexAll
     *
     * Adds a list of articles to the article datastore
     *
     * @param documents List of Articles
     */
    void indexAll(final List<Article> documents);

    /**
     * index
     *
     * Adds an article to the article datastore
     *
     * @param article Article
     */
    void index(final Article article);

    /**
     * delete
     *
     * Removes an article from the article datastore
     *
     * @param id String
     */
    void delete(final String id);
}
