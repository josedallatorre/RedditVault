package com.example.redditvault.config;

import com.example.redditvault.redditAccount.RedditAccount;
import com.example.redditvault.redditAccount.RedditAccountRepository;
import com.example.redditvault.redditPost.RedditPost;
import com.example.redditvault.redditPost.RedditPostRepository;
import com.example.redditvault.subreddit.Subreddit;
import com.example.redditvault.subreddit.SubredditRepository;
import com.example.redditvault.web.Role;
import com.example.redditvault.web.UserTest;
import com.example.redditvault.web.UserTestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
public class DataInitializer implements CommandLineRunner{

    private final SubredditRepository subredditRepository;
    private final RedditPostRepository redditPostRepository;
    private final RedditAccountRepository accountRepository;
    private final UserTestRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public DataInitializer(SubredditRepository subredditRepository,
                           RedditPostRepository redditPostRepository,
                           RedditAccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           UserTestRepository userRepository) {
        this.subredditRepository = subredditRepository;
        this.redditPostRepository = redditPostRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    @Override
    public void run(String... args) {
        var user = UserTest.builder()
                .firstname("provaname")
                .lastname("provalastname")
                .email("prova@prova.com")
                .password(passwordEncoder.encode("provetta"))
                .role(Role.valueOf("USER"))
                .build();
        userRepository.save(user);
        // 1. Create parent entities FIRST
        Subreddit subreddit = subredditRepository.save(
                new Subreddit("subreddit1")
        );

        RedditAccount account = accountRepository.save(
                new RedditAccount("user1", user)
        );

        // 2. Then create dependent entities
        RedditPost post = new RedditPost("1","test33",  "title1", "url1", subreddit, "test33");
        redditPostRepository.save(post);
    }
}
