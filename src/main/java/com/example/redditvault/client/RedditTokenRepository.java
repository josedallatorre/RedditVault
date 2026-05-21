package com.example.redditvault.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedditTokenRepository extends JpaRepository<RedditToken, Long> {
    Optional<RedditToken> findByRedditUsername(String redditUsername);
}

