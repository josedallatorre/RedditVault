package com.example.redditvault.redditAuthentication;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditStateCodeRepository extends JpaRepository<RedditStateCode, String> {
}
