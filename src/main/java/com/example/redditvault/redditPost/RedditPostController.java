package com.example.redditvault.redditPost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/post")
public class RedditPostController {
    private final RedditPostService postService;

    // Dependency Injection
    @Autowired
    public RedditPostController(RedditPostService postService) {
        this.postService = postService;
    }
    @GetMapping
    public List<RedditPost> getPosts() {
        return postService.getPosts();
    }

    @PostMapping
    public void registerPost(@RequestBody RedditPost post) {
        postService.addNewPost(post);
    }

    @DeleteMapping(path = "{postId}")
    public void deletePost(@PathVariable("postId") Long postId) {
        postService.deletePost(postId);
    }

}
