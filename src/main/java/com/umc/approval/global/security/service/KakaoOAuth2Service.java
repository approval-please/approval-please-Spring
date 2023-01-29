package com.umc.approval.global.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.approval.global.security.dto.KakaoProfile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class KakaoOAuth2Service {

    public static final String GET_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";
    public static final String KAKAO_SECRET_PASSWORD = "approval_kakao!";

    public KakaoProfile getKakaoProfileWithAccessToken(String accessToken) {
        try {

            // Header 추가
            HttpHeaders header = new HttpHeaders();
            header.add(AUTHORIZATION, "Bearer " + accessToken);
            header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // request 구성
            RestTemplate rt = new RestTemplate();
            rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(header);

            // API 요청
            ResponseEntity<String> response = rt.exchange(
                    GET_PROFILE_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            // response 객체에 매핑
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), KakaoProfile.class);

        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("SNS 로그인에 실패했습니다.");
        }
    }
}
