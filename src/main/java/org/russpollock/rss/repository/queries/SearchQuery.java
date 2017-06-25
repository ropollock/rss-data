package org.russpollock.rss.repository.queries;

import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.Arrays;

public class SearchQuery {
    private String[] fields;
    private Object searchQuery;
    private QueryBuilder filters;
    private Integer from;
    private Integer limit;

    public static enum RANGE {
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL
    }

    public SearchQuery(final SearchQueryBuilder sqb) {
        this.fields = sqb.getFields();
        this.searchQuery = sqb.getSearchQuery();
        this.from = sqb.getFrom();
        this.limit = sqb.getLimit();
        this.filters = sqb.getFilters();
    }

    public String[] getFields() {
        return fields;
    }

    public Integer getFrom() {
        return from;
    }

    public Integer getLimit() {
        return limit;
    }

    public Object getSearchQuery() {
        return searchQuery;
    }

    public QueryBuilder getFilters() {
        return filters;
    }

    public static RangeQueryBuilder buildDirectionalRangeQuery
            (final String field,
             final Object value,
             SearchQuery.RANGE direction) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery(field);
        switch (direction) {
            case GREATER_THAN:
                range.gt(value);
                break;
            case GREATER_THAN_OR_EQUAL:
                range.gte(value);
                break;
            case LESS_THAN:
                range.lt(value);
                break;
            case LESS_THAN_OR_EQUAL:
                range.lte(value);
                break;
            default:
                // @TODO throw error
        }
        return range;
    }

    @Override
    public String toString() {
        return String.format("SearchQuery [searchQuery: %s, fields: %s, filters: %s, from: %s, limit: %s]",
                this.searchQuery, Arrays.toString(this.fields), this.filters, this.from, this.limit);
    }
}
