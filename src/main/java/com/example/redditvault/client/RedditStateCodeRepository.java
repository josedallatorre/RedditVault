package com.example.redditvault.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditStateCodeRepository extends JpaRepository<RedditStateCode, String> {
}
