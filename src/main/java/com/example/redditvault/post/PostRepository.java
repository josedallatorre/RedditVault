package com.example.redditvault.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// repository because it's responsible to data access
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
