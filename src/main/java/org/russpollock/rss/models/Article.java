package org.russpollock.rss.models;

import com.google.gson.Gson;

import java.util.List;

public class Article extends Document {

    public static final String[] DEFAULT_SEARCH_FIELDS = {
            "URL",
            "content",
            "author",
            "title",
            "description",
            "source"
    };

    public String URL;
    public String published;
    public String updated;
    public String contentType;
    public String content;
    public String author;
    public String title;
    public String description;
    public List<Link> links;
    public List<Tag> tags;
    public String source;
    public String created;

    public String serializeJSON() {
        return new Gson().toJson(this);
    }

    public Article() {}
}
