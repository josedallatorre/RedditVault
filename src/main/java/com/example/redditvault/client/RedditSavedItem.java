package com.example.redditvault.client;
import com.example.redditvault.subreddit.Subreddit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditSavedItem {
    private String title;
    private String selftext;
    private String id;
    @JsonProperty("subreddit")
    private Subreddit subreddit;
    private String author;
    private boolean saved;
    @JsonProperty("secure_media")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RedditMedia secure_media;

    public RedditMedia getSecure_media() {
        return secure_media;
    }

    public void setSecure_media(RedditMedia secure_media) {
        this.secure_media = secure_media;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Subreddit getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(Subreddit subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

}
