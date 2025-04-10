package com.example.redditvault.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

// Now this is bean
@Service
public class PostService {

    private final PostRepository  postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
        /*
        return List.of(
                new Post("test1", 1L, "title1"),
                new Post("test2", 2L, "title2")
        );
        */
    }
}
