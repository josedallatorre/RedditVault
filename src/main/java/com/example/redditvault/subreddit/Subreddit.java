package com.example.redditvault.subreddit;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Subreddit {
    @Id
    private String name;

    public Subreddit() {
    }
    public Subreddit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
