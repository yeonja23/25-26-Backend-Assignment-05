package com.skhu.oauthgoogleloginpr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skhu.oauthgoogleloginpr.dto.google.GoogleTokenResponse;
import com.skhu.oauthgoogleloginpr.dto.google.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.oauth.google.client-id}")
    private String googleClientId;

    @Value("${spring.oauth.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.oauth.google.redirect-uri}")
    private String googleRedirectUri;

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    public String requestAccessToken(String code) {

        Map<String, String> params = Map.of(
                "code", code,
                "client_id", googleClientId,
                "client_secret", googleClientSecret,
                "redirect_uri", googleRedirectUri,
                "grant_type", "authorization_code"
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                GOOGLE_TOKEN_URL,
                params,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("구글 Access Token 요청 실패");
        }

        try {
            GoogleTokenResponse tokenResponse =
                    objectMapper.readValue(response.getBody(), GoogleTokenResponse.class);

            return tokenResponse.getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    public GoogleUserInfo requestUserInfo(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        URI uri = URI.create(GOOGLE_USERINFO_URL + "?access_token=" + accessToken);

        RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, uri);

        ResponseEntity<String> response = restTemplate.exchange(
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("유저 정보 요청 실패");
        }

        try {
            return objectMapper.readValue(response.getBody(), GoogleUserInfo.class);
        } catch (Exception e) {
            throw new RuntimeException("유저 정보 파싱 실패", e);
        }
    }
}
