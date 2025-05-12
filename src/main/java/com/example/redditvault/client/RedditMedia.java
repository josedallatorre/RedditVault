package com.example.redditvault.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedditMedia {
    private RedditVideo reddit_video;

    public RedditVideo getReddit_video() {
        return reddit_video;
    }

    public void setReddit_video(RedditVideo reddit_video) {
        this.reddit_video = reddit_video;
    }
}
