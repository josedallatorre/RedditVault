package com.example.redditvault.redditPost;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RedditPostConfig {
    @Bean
    CommandLineRunner commandLineRunner(RedditPostRepository repository) {
        return args -> {
            RedditPost test1 = new RedditPost("1","test33",  "title1", "url1");
            RedditPost test2 = new RedditPost("2", "test35", "title2", "url2");
            repository.saveAll(List.of(test1,test2));
        };
    }
}
