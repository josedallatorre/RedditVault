package com.example.redditvault.redditAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RedditAccountService {
    private final RedditAccountRepository redditAccountRepository;

    @Autowired
    public RedditAccountService(RedditAccountRepository redditAccountRepository) {
        this.redditAccountRepository = redditAccountRepository;
    }

    public void addNewRedditAccount(RedditAccount redditAccount) {
        Optional<RedditAccount> redditAccountOptional = redditAccountRepository.findByRedditUsername(redditAccount.getRedditUsername());
        if (redditAccountOptional.isPresent()) {
            throw new IllegalArgumentException("Reddit account already exists");
        }
        redditAccountRepository.save(redditAccount);
    }
}
