package com.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bank.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
