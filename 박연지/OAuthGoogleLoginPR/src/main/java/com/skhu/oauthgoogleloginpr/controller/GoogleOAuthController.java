package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.user.AccessTokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.global.code.SuccessStatus;
import com.skhu.oauthgoogleloginpr.global.response.BaseResponse;
import com.skhu.oauthgoogleloginpr.service.GoogleOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/callback/google")
    public BaseResponse<AccessTokenDto> googleCallback(@RequestParam("code") String code,
                                                       HttpServletResponse response) {
        TokenDto tokens = googleOAuthService.loginOrSignUp(code);

        // 리프레시 토큰을 쿠키로 내보내도록 수정
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        AccessTokenDto accessTokenDto = new AccessTokenDto(tokens.accessToken());
        return BaseResponse.onSuccess(SuccessStatus.OK, accessTokenDto);
    }
}
