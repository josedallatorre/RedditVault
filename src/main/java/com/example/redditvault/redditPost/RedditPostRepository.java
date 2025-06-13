package com.example.redditvault.redditPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// repository because it's responsible to data access
@Repository
public interface RedditPostRepository extends JpaRepository<RedditPost, Long> {
    Optional<RedditPost> findPostByAuthor(String author);
}
