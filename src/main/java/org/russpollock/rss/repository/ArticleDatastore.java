package org.russpollock.rss.repository;

import org.russpollock.rss.models.Article;
import org.russpollock.rss.models.ArticleHit;
import org.russpollock.rss.repository.queries.SearchQuery;

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
     * saveAll
     *
     * Saves a list of articles to the article datastore
     *
     * @param documents List of Articles
     */
    void saveAll(final List<Article> documents);

    /**
     * save
     *
     * Saves an article to the article datastore
     *
     * @param article Article
     */
    void save(final Article article);

    /**
     * delete
     *
     * Removes an article from the article datastore
     *
     * @param id String
     */
    void delete(final String id);
}
