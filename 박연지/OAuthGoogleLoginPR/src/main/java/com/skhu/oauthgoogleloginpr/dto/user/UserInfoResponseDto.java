package com.skhu.oauthgoogleloginpr.dto.user;

import com.skhu.oauthgoogleloginpr.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserInfoResponseDto(
        Long id,
        String name,
        String email,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
