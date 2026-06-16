package com.example.redditvault.web;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTestRepository extends JpaRepository<UserTest, Integer> {
    Optional<UserTest> findByEmail(String email);
    Optional<UserTest> findById(Integer id);
}
