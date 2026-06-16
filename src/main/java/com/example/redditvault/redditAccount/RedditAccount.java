package com.example.redditvault.redditAccount;

import com.example.redditvault.redditAuthentication.RedditToken;
import com.example.redditvault.web.UserTest;
import jakarta.persistence.*;

import java.util.Set;

@Entity
public class RedditAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String redditUsername;
    @ManyToOne
    private UserTest userTest;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "redditAccount")
    private Set<RedditToken> tokens;

    public RedditAccount() {

    }
    public RedditAccount(String redditUsername, UserTest userTest) {
        this.redditUsername = redditUsername;
        this.userTest = userTest;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRedditUsername() {
        return redditUsername;
    }

    public void setRedditUsername(String redditUsername) {
        this.redditUsername = redditUsername;
    }

    public UserTest getUserTest() {
        return userTest;
    }

    public void setUserTest(UserTest userTest) {
        this.userTest = userTest;
    }

    public Set<RedditToken> getTokens() {
        return tokens;
    }

    public void setTokens(Set<RedditToken> tokens) {
        this.tokens = tokens;
    }
}
