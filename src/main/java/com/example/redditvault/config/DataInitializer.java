package com.example.redditvault.config;

import com.example.redditvault.redditAccount.RedditAccount;
import com.example.redditvault.redditAccount.RedditAccountRepository;
import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.redditPost.RedditPostRepository;
import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataInitializer implements CommandLineRunner{

    private final SubredditRepository subredditRepository;
    private final RedditPostRepository redditPostRepository;
    private final RedditAccountRepository accountRepository;

    public DataInitializer(SubredditRepository subredditRepository,
                           RedditPostRepository redditPostRepository,
                           RedditAccountRepository accountRepository) {
        this.subredditRepository = subredditRepository;
        this.redditPostRepository = redditPostRepository;
        this.accountRepository = accountRepository;
    }
    @Override
    public void run(String... args) {
        // 1. Create parent entities FIRST
        Subreddit subreddit = subredditRepository.save(
                new Subreddit("subreddit1")
        );

        RedditAccount account = accountRepository.save(
                new RedditAccount("user1")
        );

        // 2. Then create dependent entities
        RedditPost post = new RedditPost("1","test33",  "title1", "url1", new Subreddit("subreddit1"), "test33");
        redditPostRepository.save(post);
    }
}
