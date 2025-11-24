package com.skhu.oauthgoogleloginpr.dto.user;

public record SignupRequestDto(
        String name,
        String email,
        String password
) {
}
