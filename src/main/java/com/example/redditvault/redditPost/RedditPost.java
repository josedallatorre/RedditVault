package com.example.redditvault.redditPost;
import com.example.redditvault.client.Media;
import jakarta.persistence.*;
import java.util.List;



@Entity
@Table
public class RedditPost {
    @Id
    private String id;
    private String author;
    private String title;
    private String url;
    private String subredditId;
    private String redditUsername;

    @OneToMany(mappedBy = "redditPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> mediaList;

    public RedditPost() {}

    public RedditPost(String id,String author,
                      String title, String url,
                      String subredditId, String redditUsername) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.url = url;
        this.subredditId = subredditId;
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

    public String getSubredditId() {
        return subredditId;
    }

    public void setSubredditId(String subredditId) {
        this.subredditId = subredditId;
    }

    public String getRedditUsername() {
        return redditUsername;
    }

    public void setRedditUsername(String redditUsername) {
        this.redditUsername = redditUsername;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
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
