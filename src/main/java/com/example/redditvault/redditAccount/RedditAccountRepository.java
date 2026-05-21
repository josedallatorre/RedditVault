package com.example.redditvault.redditAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedditAccountRepository extends JpaRepository<RedditAccount, Long> {
    Optional<RedditAccount> findByRedditUsername(String redditUsername);
}
