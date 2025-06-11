package com.example.redditvault.subreddit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubredditService {
    private final SubredditRepository subredditRepository;

    @Autowired
    public SubredditService(SubredditRepository subredditRepository){
        this.subredditRepository = subredditRepository;
    }

    public List<Subreddit> getAllSubreddits(){
        return subredditRepository.findAll();
    }

    public void addNewSubreddit(Subreddit subreddit) {
        /*
        Optional<Subreddit> subredditOptional =  subredditRepository.findsubredditByName(subreddit.getName());

        if (subredditOptional.isPresent()) {
            throw new IllegalStateException("Subreddit name already exists");
        }
        */
        subredditRepository.save(subreddit);
        System.out.println(subreddit);
    }

}
