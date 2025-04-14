package com.example.redditvault.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Now this is bean
@Service
public class PostService {

    private final PostRepository  postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public void addNewPost(Post post) {
        Optional<Post> postOptional =  postRepository.findPostByAuthor(post.getAuthor());
        if (postOptional.isPresent()) {
            throw new IllegalStateException("Post author already exists");
        }
        postRepository.save(post);
        System.out.println(post);
    }
}
