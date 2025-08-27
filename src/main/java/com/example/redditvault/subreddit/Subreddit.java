package com.example.redditvault.subreddit;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Subreddit {
    @Id
    private String subredditId;

    private String name;

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
