package com.example.redditvault.redditPost;

import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RedditPostTests {
    @Autowired
    private RedditPostRepository redditPostRepository;

    @Autowired
    private SubredditRepository subredditRepository;

    @Test
    public void testExistsById() {
        Subreddit subreddit = new Subreddit("subreddit1");
        subredditRepository.save(subreddit); // persist it first
        RedditPost post = new RedditPost("1","test33",  "title1", "url1", subreddit, "test33");
        post = redditPostRepository.save(post);
        boolean exists = redditPostRepository.existsById(Long.valueOf(post.getId()));
        assertThat(exists).isTrue();
    }
}
