package org.russpollock.rss.repository.queries;

import org.elasticsearch.index.query.QueryBuilder;

public class SearchQueryBuilder {
    private String[] fields;
    private Object searchQuery;
    private QueryBuilder filters;
    private Integer from = 0;
    private Integer limit = 10;

    public SearchQueryBuilder() {}

    public SearchQueryBuilder setFields(final String... fields) {
        this.fields = fields;
        return this;
    }

    public SearchQueryBuilder setSearchQuery(Object value) {
        this.searchQuery = value;
        return this;
    }

    public SearchQueryBuilder setFrom(final int from) {
        this.from = (from < 0) ? 0 : from;
        return this;
    }

    public SearchQueryBuilder setLimit(final int limit) {
        this.limit = (limit < 0) ? 0 : limit;
        return this;
    }

    public SearchQueryBuilder setFilters(final QueryBuilder filters) {
        this.filters = filters;
        return this;
    }

    public String[] getFields() {
        return fields;
    }

    public Object getSearchQuery() {
        return searchQuery;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getLimit() {
        return limit;
    }

    public QueryBuilder getFilters() {
        return filters;
    }

    public SearchQuery build() {
        return new SearchQuery(this);
    }
}
