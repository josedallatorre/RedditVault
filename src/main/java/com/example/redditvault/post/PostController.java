package com.example.redditvault.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/post")
public class PostController {
    private final PostService postService;

    // Dependency Injection
    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }
    @GetMapping
    public List<Post> getPosts() {
        return postService.getPosts();
    }

    @PostMapping
    public void registerPost(@RequestBody Post post) {
        postService.addNewPost(post);
    }

    @DeleteMapping(path = "{postId}")
    public void deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
    }

}
