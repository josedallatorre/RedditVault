package com.example.redditvault.redditPost;
import jakarta.persistence.*;


@Entity
@Table
public class RedditPost {
    @Id
    private String id;
    private String author;
    private String title;
    private String url;
    private String subreddit;
    private String redditUsername;

    public RedditPost() {}

    public RedditPost(String id,String author,
                      String title, String url,
                      String subreddit, String redditUsername) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
        this.subreddit = subreddit;
        this.redditUsername = redditUsername;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubreddit() {
        return subreddit;
    }
    public void setSubreddit(String subreddit) {this.subreddit = subreddit;}

    public String getRedditUsername() {
        return redditUsername;
    }

    public void setRedditUsername(String redditUsername) {
        this.redditUsername = redditUsername;
    }

    @Override
    public String toString() {
        return "Post{" +
                "author='" + author + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
