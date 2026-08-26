package com.example.redditvault.redditPost;

import com.example.redditvault.subreddit.Subreddit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RedditPostTests {
    @Autowired
    private RedditPostRepository redditPostRepository;

    @Test
    public void testExistsById() {
        Subreddit subreddit = new Subreddit("subreddit1");
        RedditPost post = new RedditPost("1","test33",  "title1", "url1", subreddit, "test33");
        post = redditPostRepository.save(post);
        boolean exists = redditPostRepository.existsById(Long.valueOf(post.getId()));
        assertThat(exists).isTrue();
    }
}
