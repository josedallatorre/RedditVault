package com.example.redditvault.subreddit;

import com.example.redditvault.redditPost.RedditPost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Entity
@Table
public class Subreddit {
    @Id
    private String subredditId;
    private String name;
    @OneToMany(fetch = FetchType.LAZY)
    private Set<RedditPost> redditPost;

    public Subreddit() {
    }
    public Subreddit(String subredditId) {
        this.subredditId = subredditId;
    }
    public void setSubredditId(String subredditId) {
        this.subredditId = subredditId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
