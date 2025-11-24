package com.skhu.oauthgoogleloginpr.service;

import com.skhu.oauthgoogleloginpr.domain.Post;
import com.skhu.oauthgoogleloginpr.domain.Role;
import com.skhu.oauthgoogleloginpr.domain.User;
import com.skhu.oauthgoogleloginpr.dto.post.PostInfoResponseDto;
import com.skhu.oauthgoogleloginpr.dto.post.PostSaveRequestDto;
import com.skhu.oauthgoogleloginpr.global.code.ErrorStatus;
import com.skhu.oauthgoogleloginpr.global.exception.GeneralException;
import com.skhu.oauthgoogleloginpr.repository.PostRepository;
import com.skhu.oauthgoogleloginpr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostInfoResponseDto createPost(PostSaveRequestDto requestDto, Long userId) {
        User user = getUser(userId);

        Post post = Post.builder()
                .title(requestDto.title())
                .content(requestDto.content())
                .user(user)
                .build();

        postRepository.save(post);
        return PostInfoResponseDto.from(post);
    }

    @Transactional(readOnly = true)
    public List<PostInfoResponseDto> findAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(PostInfoResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostInfoResponseDto findPostById(Long postId) {
        Post post = getPost(postId);
        return PostInfoResponseDto.from(post);
    }

    @Transactional
    public PostInfoResponseDto updatePost(Long postId, PostSaveRequestDto requestDto, Long userId) {
        Post post = getPost(postId);
        User user = getUser(userId);

        checkPostAccess(post, user);

        post.update(requestDto.title(), requestDto.content());
        return PostInfoResponseDto.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = getPost(postId);

        User user = getUser(userId);

        checkPostAccess(post, user);

        postRepository.delete(post);
    }

    // 헬퍼 메소드들
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
    }

    private void checkPostAccess(Post post, User user) {
        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new GeneralException(ErrorStatus.POST_ACCESS_DENIED);
        }
    }
}
