package com.example.redditvault.subreddit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, String> {
    //Optional<Subreddit> findsubredditByName(String subredditName);
}
