package com.example.redditvault.client;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditSavedItem {
    private String title;
    private String selftext;
    private String subreddit;
    private String author_fullname;
    private boolean saved;
    private String secure_media;

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor_fullname() {
        return author_fullname;
    }

    public void setAuthor_fullname(String author_fullname) {
        this.author_fullname = author_fullname;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getSecure_media() {
        return secure_media;
    }

    public void setSecure_media(String secure_media) {
        this.secure_media = secure_media;
    }
}
