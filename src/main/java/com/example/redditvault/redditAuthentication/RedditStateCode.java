package com.example.redditvault.redditAuthentication;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class RedditStateCode {
    @Id
    private String id;
    private Integer userId;

    public RedditStateCode(String id, Integer userId) {
        this.id = id;
        this.userId = userId;
    }

    public RedditStateCode() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
