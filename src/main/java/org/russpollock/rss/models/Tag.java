package org.russpollock.rss.models;

public class Tag {
    public String tag;
    public String tagType;

    public Tag() {}

    public Tag(final String tag, final String tagType) {
        this.tag = tag;
        this.tagType = tagType;
    }
}
