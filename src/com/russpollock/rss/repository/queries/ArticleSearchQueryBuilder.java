package com.russpollock.rss.repository.queries;

import com.russpollock.rss.models.Tag;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import java.util.ArrayList;

public class ArticleSearchQueryBuilder extends SearchQueryBuilder {
    private BoolQueryBuilder filters;

    public ArticleSearchQueryBuilder() {}

    public BoolQueryBuilder getFilters() {
        return this.filters;
    }

    public ArticleSearchQueryBuilder setContentType(final String contentType) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("contentType", contentType);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    /**
     * setAuthor
     *
     * Add term query on author.raw field (not analyzed)
     *
     * @param author
     * @return
     */
    public ArticleSearchQueryBuilder setAuthor(final String author) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("author.raw", author);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    public ArticleSearchQueryBuilder setTitle(final String title) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("title.raw", title);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    public ArticleSearchQueryBuilder setCreated(final String date, SearchQuery.RANGE direction) {
        RangeQueryBuilder range = SearchQuery.buildDirectionalRangeQuery("created", date, direction);
        return setCreated(range);
    }

    public ArticleSearchQueryBuilder setCreated(final String from, final String to) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery("created").from(from).to(to);
        return setCreated(range);
    }

    private ArticleSearchQueryBuilder setCreated(final RangeQueryBuilder range) {
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, range);
        return this;
    }

    public ArticleSearchQueryBuilder setPublished(final String date, SearchQuery.RANGE direction) {
        RangeQueryBuilder range = SearchQuery.buildDirectionalRangeQuery("published", date, direction);
        return setPublished(range);
    }

    public ArticleSearchQueryBuilder setPublished(final String from, final String to) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery("published").from(from).to(to);
        return setPublished(range);
    }

    private ArticleSearchQueryBuilder setPublished(final RangeQueryBuilder range) {
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, range);
        return this;
    }

    public ArticleSearchQueryBuilder setTags(final ArrayList<Tag> tags) {
        if(filters == null) {
            filters = QueryBuilders.boolQuery();
        }

        for(Tag tag: tags) {
            filters = filters.should(ArticleSearchQueryBuilder.buildTagQuery(tag));
        }
        return this;
    }

    public ArticleSearchQueryBuilder setTags(final Tag tag) {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(tag);
        return setTags(tags);
    }

    public ArticleSearchQuery build() {
        return new ArticleSearchQuery(this);
    }

    public static BoolQueryBuilder buildTagQuery(final Tag tag) {
        return QueryBuilders.boolQuery().must(
                QueryBuilders.termQuery("tags.tag", tag.tag))
                .must(QueryBuilders.termQuery("tags.tagType", tag.tagType));
    }

    public static BoolQueryBuilder addMustQueryToFilters(BoolQueryBuilder builder, QueryBuilder query) {
        if(builder == null) {
            return QueryBuilders.boolQuery().must(query);
        } else {
            return builder.must(query);
        }
    }
}
