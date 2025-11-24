package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.post.PostInfoResponseDto;
import com.skhu.oauthgoogleloginpr.dto.post.PostSaveRequestDto;
import com.skhu.oauthgoogleloginpr.global.code.SuccessStatus;
import com.skhu.oauthgoogleloginpr.global.response.BaseResponse;
import com.skhu.oauthgoogleloginpr.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public BaseResponse<PostInfoResponseDto> createPost(
            @RequestBody PostSaveRequestDto requestDto,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        return BaseResponse.onSuccess(SuccessStatus.CREATED, postService.createPost(requestDto, userId));
    }

    @GetMapping
    public BaseResponse<Page<PostInfoResponseDto>> getAllPosts(Pageable pageable) {
        return BaseResponse.onSuccess(SuccessStatus.OK, postService.findAllPosts(pageable));
    }

    @GetMapping("/{postId}")
    public BaseResponse<PostInfoResponseDto> getPostById(@PathVariable Long postId) {
        return BaseResponse.onSuccess(SuccessStatus.OK, postService.findPostById(postId));
    }

    @PatchMapping("/{postId}")
    public BaseResponse<PostInfoResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestBody PostSaveRequestDto requestDto,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        return BaseResponse.onSuccess(SuccessStatus.OK, postService.updatePost(postId, requestDto, userId));
    }

    @DeleteMapping("/{postId}")
    public BaseResponse<Void> deletePost(
            @PathVariable Long postId,
            Principal principal
    ) {
        Long userId = Long.parseLong(principal.getName());
        postService.deletePost(postId, userId);
        return BaseResponse.onSuccess(SuccessStatus.OK, null);
    }
}
