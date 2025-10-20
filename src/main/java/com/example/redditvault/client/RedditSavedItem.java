package com.example.redditvault.client;
import com.example.redditvault.subreddit.Subreddit;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

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
    private String url;
    private String permalink;
    private String is_video;
    @JsonProperty("is_reddit_media_domain")
    private String is_reddit_media_domain;
    private String url_overridden_by_dest;
    private String is_gallery;
    private Map<String, MediaMetadata> media_metadata;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getPermalink() {return permalink;}
    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public boolean getIsVideo() {
        if (is_video != null && !is_video.equals("")) {
            return is_video.equals("true");
        }
        return false;
    }

    public void setIsVideo(String is_video) {
        this.is_video = is_video;
    }
    public boolean getIsRedditMediaDomain() {
        if (is_reddit_media_domain != null && !is_reddit_media_domain.equals("")) {
            return is_reddit_media_domain.equals("true");
        }
        return false;
    }
    public void setIsRedditMediaDomain(String is_reddit_media_domain) {
        this.is_reddit_media_domain = is_reddit_media_domain;
    }
    public String getUrl_overridden_by_dest() {
        if (url_overridden_by_dest != null && !url_overridden_by_dest.equals("")) {
            return url_overridden_by_dest;
        }
        return "";
    }
    public void setUrl_overridden_by_dest(String url_overridden_by_dest) {
        this.url_overridden_by_dest = url_overridden_by_dest;
    }

    public boolean getIsGallery() {
        if (is_gallery != null && !is_gallery.equals("")) {
            return is_gallery.equals("true");
        }
        return false;
        }

    public void setIs_gallery(String is_gallery) {
        this.is_gallery = is_gallery;
    }

    public Map<String, MediaMetadata> getMedia_metadata() {
        return media_metadata;
    }

    public void setMedia_metadata(Map<String, MediaMetadata> media_metadata) {
        this.media_metadata = media_metadata;
    }
}
