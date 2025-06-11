package com.example.redditvault.subreddit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SubredditConfig {
    @Bean
    CommandLineRunner commandLineRunner1(SubredditRepository repository) {
        return args -> {
            Subreddit test1 = new Subreddit("subreddit1");
            Subreddit test2 = new Subreddit("subreddit2");
            repository.saveAll(List.of(test1,test2));
        };
    }
}
