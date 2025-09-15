package com.example.redditvault.client;

import com.example.redditvault.redditPost.RedditPost;
import jakarta.persistence.*;

@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // local DB id

    private String redditImageId;   // Reddit's image id inside the gallery
    private String redditGalleryId; // Reddit's gallery id (null if not a gallery)

    private String type; // "image", "video", "gallery"
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reddit_post_id")
    private RedditPost redditPost;

    public Media() {}

    public Media(String redditImageId, String redditGalleryId, String type, String url, RedditPost redditPost) {
        this.redditImageId = redditImageId;
        this.redditGalleryId = redditGalleryId;
        this.type = type;
        this.url = url;
        this.redditPost = redditPost;
    }

    public String getRedditImageId() {
        return redditImageId;
    }

    public void setRedditImageId(String redditImageId) {
        this.redditImageId = redditImageId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedditGalleryId() {
        return redditGalleryId;
    }

    public void setRedditGalleryId(String redditGalleryId) {
        this.redditGalleryId = redditGalleryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RedditPost getRedditPost() {
        return redditPost;
    }

    public void setRedditPost(RedditPost redditPost) {
        this.redditPost = redditPost;
    }
}


