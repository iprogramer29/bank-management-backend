package com.bank.controller;

import org.springframework.web.bind.annotation.*;

import com.bank.entity.Post;
import com.bank.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping
	public List<Post> getAllPosts() {
		return postService.getAllPosts();
	}

	@PostMapping
	public Post createPost(@RequestBody Post post) {
		return postService.createPost(post);
	}

	@DeleteMapping("/{id}")
	public void deletePost(@PathVariable Long id) {
		postService.deletePost(id);
	}
}
