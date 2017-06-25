package org.russpollock.rss.models.builders;

import org.russpollock.rss.models.Article;
import org.russpollock.rss.models.Link;
import org.russpollock.rss.models.Tag;

import java.util.ArrayList;
import java.util.List;

public class ArticleBuilder {
    private String URL;
    private String author;
    private String contentType;
    private String published;
    private String updated;
    private String content;
    private String title;
    private String description;
    private String source;
    private String created;
    private List<Tag> tags;
    private List<Link> links;

    public ArticleBuilder() {}

    public ArticleBuilder setURL(final String URL) {
        this.URL = URL;
        return this;
    }

    public ArticleBuilder setAuthor(final String author) {
        this.author = author;
        return this;
    }

    public ArticleBuilder setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ArticleBuilder setPublished(final String published) {
        this.published = published;
        return this;
    }

    public ArticleBuilder setUpdated(final String updated) {
        this.updated = updated;
        return this;
    }

    public ArticleBuilder setCreated(final String created) {
        this.created = created;
        return this;
    }

    public ArticleBuilder setContent(final String content) {
        this.content = content;
        return this;
    }

    public ArticleBuilder setTile(final String title) {
        this.title = title;
        return this;
    }

    public ArticleBuilder setDescription(final String description) {
        this.description = description;
        return this;
    }

    public ArticleBuilder setSource(final String source) {
        this.source = source;
        return this;
    }

    public ArticleBuilder addTag(final Tag tag) {
        if(this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
        return this;
    }

    public ArticleBuilder addTag(final String tag, final String tagType) {
        if(this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(new Tag(tag, tagType));
        return this;
    }

    public ArticleBuilder addLink(final Link link) {
        if(this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(link);
        return this;
    }

    public ArticleBuilder addLink(final String href) {
        if(this.links == null) {
            this.links = new ArrayList<>();
        }
        Link l = new Link();
        l.href = href;
        this.links.add(l);
        return this;
    }

    public Article build() {
        Article a = new Article();
        a.setID(this.URL);
        a.URL = this.URL;
        a.description = this.description;
        a.title = this.title;
        a.author = this.author;
        a.source = this.source;
        a.content = this.content;
        a.contentType = this.contentType;
        a.created = this.created;
        a.published = this.published;
        a.updated = this.updated;
        a.links = this.links;
        a.tags = this.tags;
        return a;
    }
}
