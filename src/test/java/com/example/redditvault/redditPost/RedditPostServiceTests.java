package com.example.redditvault.redditPost;

import com.example.redditvault.subreddit.Subreddit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedditPostServiceTests {
    @Mock
    private RedditPostRepository redditPostRepository;
    //When using Mockito Use @InjectMocks to inject Mocked beans to following class
    @InjectMocks
    private RedditPostService redditPostService;

    @Test
    void getAllPerson() {
        Subreddit subreddit = new Subreddit("subreddit1");
        RedditPost post1 = new RedditPost("1", "test33", "title1", "url1", subreddit, "test33");
        RedditPost post2 = new RedditPost("2", "test33", "title1", "url1", subreddit, "test33");
        given(redditPostRepository.findAll()).willReturn(List.of(post1, post2));
        List<RedditPost> personList = redditPostService.getPosts();
        assertThat(personList).isNotNull();
        assertThat(personList.size()).isEqualTo(2);
        verify(redditPostRepository).findAll(); // Optional but recommended
    }
}
