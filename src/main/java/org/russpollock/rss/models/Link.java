package org.russpollock.rss.models;

public class Link {
    public String href;
    public String rel;
    public String type;

    public Link() {}

    public Link(final String href, final String rel, final String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }
}
