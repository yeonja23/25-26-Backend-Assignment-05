package com.skhu.oauthgoogleloginpr.dto.post;

import com.skhu.oauthgoogleloginpr.domain.Post;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostInfoResponseDto(
        Long id,
        String title,
        String content,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PostInfoResponseDto from(Post post) {
        return PostInfoResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .name(post.getUser().getName())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
