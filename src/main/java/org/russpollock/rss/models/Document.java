package org.russpollock.rss.models;

public abstract class Document {
    private String id;

    public String getID() {
        return this.id;
    }

    public void setID(final String id) {
        this.id = id;
    }
}
