package com.skhu.oauthgoogleloginpr.dto.user;

import lombok.Builder;

@Builder
public record AccessTokenDto(
        String accessToken
) {
}
