package com.example.redditvault.client;

import com.example.redditvault.redditPost.RedditPost;

import java.util.List;

public class SavedPageResponse {
    private List<RedditPost> posts;
    private String after;

    public SavedPageResponse(String after, List<RedditPost> posts) {
        this.after = after;
        this.posts = posts;
    }

    public List<RedditPost> getPosts() {
        return posts;
    }

    public void setPosts(List<RedditPost> posts) {
        this.posts = posts;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }
}
