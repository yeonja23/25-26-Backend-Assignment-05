package com.skhu.oauthgoogleloginpr.controller;

import com.skhu.oauthgoogleloginpr.dto.user.AccessTokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.LoginRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.RefreshTokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.SignupRequestDto;
import com.skhu.oauthgoogleloginpr.dto.user.TokenDto;
import com.skhu.oauthgoogleloginpr.dto.user.UserInfoResponseDto;
import com.skhu.oauthgoogleloginpr.global.code.SuccessStatus;
import com.skhu.oauthgoogleloginpr.global.response.BaseResponse;
import com.skhu.oauthgoogleloginpr.service.AuthService;
import com.skhu.oauthgoogleloginpr.service.TokenService;
import com.skhu.oauthgoogleloginpr.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public BaseResponse<UserInfoResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        return BaseResponse.onSuccess(SuccessStatus.CREATED, userService.signup(requestDto));
    }

    @PostMapping("/login")
    public BaseResponse<AccessTokenDto> login(@RequestBody LoginRequestDto requestDto,
                                              HttpServletResponse response) {
        TokenDto tokens = authService.login(requestDto);

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

    @PostMapping("/reissue")
    public BaseResponse<AccessTokenDto> reissue(
            @CookieValue("refresh_token") String refreshToken
    ) {
        TokenDto newTokens = tokenService.reissueAccessToken(refreshToken);

        return BaseResponse.onSuccess(SuccessStatus.OK, new AccessTokenDto(newTokens.accessToken()));
    }
}
