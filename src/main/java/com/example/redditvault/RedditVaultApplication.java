package com.example.redditvault;

import com.example.redditvault.post.Post;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class RedditVaultApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedditVaultApplication.class, args);
    }
    @GetMapping
    public List<Post> hello() {
        return List.of(
                new Post("test1", "1", "title1"),
                new Post("test2", "2", "title2")
        );
    }

}
