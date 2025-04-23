package com.example.redditvault.post;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PostConfig {
    @Bean
    CommandLineRunner commandLineRunner(PostRepository repository) {
        return args -> {
            Post test1 = new Post("test33",  "title1");
            Post test2 = new Post("test35",  "title2");
            repository.saveAll(List.of(test1,test2));
        };
    }
}
