package com.example.redditvault.post;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

// Now this is bean
@Service
public class PostService {
    public List<Post> getPosts() {
        return List.of(
                new Post("test1", "1", "title1"),
                new Post("test2", "2", "title2")
        );
    }
}
