package com.example.redditvault.redditPost;
import com.example.redditvault.subreddit.Subreddit;
import jakarta.persistence.*;

@Entity
@Table
public class RedditPost {
    @Id
    private String id;
    private String author;
    private String title;
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    private Subreddit subreddit;
    private String redditUsername;

    public RedditPost() {}

    public RedditPost(String id,String author,
                      String title, String url,
                      Subreddit subreddit, String redditUsername) {
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

    public Subreddit getSubredditId() {
        return subreddit;
    }

    public void setSubredditId(Subreddit subredditId) {
        this.subreddit = subredditId;
    }

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
