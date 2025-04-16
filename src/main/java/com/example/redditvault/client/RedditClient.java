package com.example.redditvault.client;

public class RedditClient {
    private String id;
    private String username;
    private boolean isOver18;

    public RedditClient(String id, String username, boolean isOver18) {
        this.id = id;
        this.username = username;
        this.isOver18 = isOver18;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOver18() {
        return isOver18;
    }

    public void setOver18(boolean over18) {
        isOver18 = over18;
    }

    @Override
    public String toString() {
        return "RedditClient{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", isOver18=" + isOver18 +
                '}';
    }
}
