package com.russpollock.rss.repository;

import com.russpollock.rss.models.Article;
import com.russpollock.rss.models.ArticleHit;
import com.russpollock.rss.repository.queries.SearchQuery;

import java.util.List;

public interface ArticleDatastore {
    List<ArticleHit> search(SearchQuery query);
    void indexAll(final List<Article> documents);
    void index(final Article article);
    void delete(final String id);
}
