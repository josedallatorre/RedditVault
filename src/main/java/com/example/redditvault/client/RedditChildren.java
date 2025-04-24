package com.example.redditvault.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditChildren {
    @JsonProperty("data")
    private RedditSavedItem redditSavedItem;

    public RedditSavedItem getRedditSavedItem() {
        return redditSavedItem;
    }

    public void setRedditSavedItem(RedditSavedItem redditSavedItem) {
        this.redditSavedItem = redditSavedItem;
    }
}

