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

    /**
     * setContentType
     *
     * Add term query on `contentType`  field (not analyzed) to post filters
     *
     * @param contentType String
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setContentType(final String contentType) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("contentType", contentType);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    /**
     * setAuthor
     *
     * Add term query on `author.raw` field (not analyzed) to post filters
     *
     * @param author String
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setAuthor(final String author) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("author.raw", author);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    /**
     * setTitle
     *
     * Add term query on `title.raw` field (not analyzed) to post filters
     *
     * @param title String
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setTitle(final String title) {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("title.raw", title);
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, termQuery);
        return this;
    }

    /**
     * setCreated
     *
     * Add directional range query to the post filters for `created`.
     *
     * @param date String
     * @param direction SearchQuery.RANGE
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setCreated(final String date, SearchQuery.RANGE direction) {
        RangeQueryBuilder range = SearchQuery.buildDirectionalRangeQuery("created", date, direction);
        return setCreated(range);
    }

    /**
     * setCreated
     *
     * Add a range query to the post filters for `created`.
     *
     * @param from String
     * @param to String
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setCreated(final String from, final String to) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery("created").from(from).to(to);
        return setCreated(range);
    }

    private ArticleSearchQueryBuilder setCreated(final RangeQueryBuilder range) {
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, range);
        return this;
    }

    /**
     * setPublished
     *
     * Add directional range query to the post filters for `published`.
     *
     * @param date String
     * @param direction SearchQuery.RANGE
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setPublished(final String date, SearchQuery.RANGE direction) {
        RangeQueryBuilder range = SearchQuery.buildDirectionalRangeQuery("published", date, direction);
        return setPublished(range);
    }

    /**
     * setPublished
     *
     * Add a range query to the post filters for `published`.
     *
     * @param from String
     * @param to String
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setPublished(final String from, final String to) {
        RangeQueryBuilder range = QueryBuilders.rangeQuery("published").from(from).to(to);
        return setPublished(range);
    }

    private ArticleSearchQueryBuilder setPublished(final RangeQueryBuilder range) {
        this.filters = ArticleSearchQueryBuilder.addMustQueryToFilters(this.filters, range);
        return this;
    }

    /**
     * setTags
     *
     * Adds a list of tag BoolQueries to the post filter should filters.
     *
     * @param tags An ArrayList of Tag
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setTags(final ArrayList<Tag> tags) {
        if(filters == null) {
            filters = QueryBuilders.boolQuery();
        }

        for(Tag tag: tags) {
            filters = filters.should(ArticleSearchQueryBuilder.buildTagQuery(tag));
        }
        return this;
    }

    /**
     * setTags
     *
     * Adds a tag BoolQuery to the post filter should filters.
     *
     * @param tag Tag
     * @return ArticleSearchQueryBuilder
     */
    public ArticleSearchQueryBuilder setTags(final Tag tag) {
        ArrayList<Tag> tags = new ArrayList<Tag>();
        tags.add(tag);
        return setTags(tags);
    }

    /**
     * build
     *
     * Builds an ArticleSearchQuery
     *
     * @return ArticleSearchQuery
     */
    public ArticleSearchQuery build() {
        return new ArticleSearchQuery(this);
    }

    /**
     * buildTagQuery
     *
     * Builds a bool query to match the given Tag.
     * Field queried are `tags.tag` and `tags.tagType`
     *
     * @param tag Tag
     * @return BoolQueryBuilder
     */
    public static BoolQueryBuilder buildTagQuery(final Tag tag) {
        return QueryBuilders.boolQuery().must(
                QueryBuilders.termQuery("tags.tag", tag.tag))
                .must(QueryBuilders.termQuery("tags.tagType", tag.tagType));
    }

    /**
     * addMustQueryToFilters
     *
     * Adds a QueryBuilder to a BoolQueryBuilder must.
     *
     * @param builder BoolQueryBuilder
     * @param query QuilderBuilder to add to builder must
     * @return BoolQueryBuilder
     */
    public static BoolQueryBuilder addMustQueryToFilters(BoolQueryBuilder builder, QueryBuilder query) {
        if(builder == null) {
            return QueryBuilders.boolQuery().must(query);
        } else {
            return builder.must(query);
        }
    }
}
