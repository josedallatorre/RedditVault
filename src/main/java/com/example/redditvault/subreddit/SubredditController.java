package com.example.redditvault.subreddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/subreddit")
public class SubredditController {
    private final SubredditService subredditService;

    // Dependency Injection
    @Autowired
    public SubredditController(SubredditService subredditService) {
        this.subredditService = subredditService;
    }
    @GetMapping
    public List<Subreddit> getSubreddits() {
        return subredditService.getAllSubreddits();
    }

    @PostMapping
    public void registerSubreddit(@RequestBody Subreddit subreddit) {
        subredditService.addNewSubreddit(subreddit);
    }
    /*
    @DeleteMapping(path = "{subredditName}")
    public void deleteSubreddit(@PathVariable("subredditName") String subredditName) {
        subredditService.deleteSubreddit(subredditName);
    }
    */

}
