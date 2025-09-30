package com.mongsom.dev.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.respDto.NaverLoginRespDto;
import com.mongsom.dev.dto.auth.respDto.NaverProfileRespDto;
import com.mongsom.dev.dto.auth.respDto.NaverTokenRespDto;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverAuthService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    
    @Value("${naver.client-id}")
    private String clientId;
    
    @Value("${naver.client-secret}")
    private String clientSecret;
    
    /**
     * 네이버 로그인: 인가 코드로 Access Token 발급 후 프로필 조회
     * @param code 네이버 인가 코드
     * @param state 상태 토큰
     * @return 네이버 프로필 정보
     */
    public RespDto<NaverLoginRespDto> getNaverProfile(String code, String state) {
        try {
            log.info("=== 네이버 로그인 시작 ===");
            log.info("Code: {}, State: {}", code, state);
            
            // 1. Access Token 발급
            NaverTokenRespDto tokenResponse = getAccessToken(code, state);
            
            if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
                log.error("Access Token 발급 실패");
                return RespDto.<NaverLoginRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            log.info("Access Token 발급 성공");
            
            // 2. Access Token으로 프로필 조회
            NaverProfileRespDto profileResponse = getUserProfile(tokenResponse.getAccessToken());
            
            if (profileResponse == null || !"00".equals(profileResponse.getResultCode())) {
                log.error("프로필 조회 실패");
                return RespDto.<NaverLoginRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            String email = profileResponse.getResponse().getEmail();
            String name = profileResponse.getResponse().getName();
            
            log.info("프로필 조회 성공 - email: {}, name: {}", email, name);
            
            // 3. 기존 회원 여부 확인 (email, name, provider=NAVER)
            Optional<User> userOpt = userRepository.findByEmailAndName(email, name);
            
            if (userOpt.isPresent()) {
                // 기존 회원 - userCode 반환
                User user = userOpt.get();
                Long userCode = user.getUserCode();
                
                log.info("=== 기존 회원 확인 ===");
                log.info("UserCode: {}, Email: {}, Name: {}", userCode, email, name);
                
                NaverLoginRespDto responseDto = NaverLoginRespDto.builder()
                        .userCode(userCode)
                        .profile(null)
                        .build();
                
                return RespDto.<NaverLoginRespDto>builder()
                        .code(1)  // 기존 회원
                        .data(responseDto)
                        .build();
                
            } else {
                // 신규 회원 - 프로필 정보 반환
                log.info("=== 신규 회원 - 프로필 정보 반환 ===");
                
                NaverLoginRespDto responseDto = NaverLoginRespDto.builder()
                        .userCode(null)
                        .profile(profileResponse)
                        .build();
                
                return RespDto.<NaverLoginRespDto>builder()
                        .code(2)  // 신규 회원
                        .data(responseDto)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("네이버 로그인 실패", e);
            return RespDto.<NaverLoginRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    /**
     * 네이버 Access Token 발급
     */
    private NaverTokenRespDto getAccessToken(String code, String state) {
        try {
            log.info("=== Access Token 발급 요청 ===");
            
            String url = "https://nid.naver.com/oauth2.0/token";
            
            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("state", state);
            
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            // API 호출
            ResponseEntity<NaverTokenRespDto> response = restTemplate.postForEntity(
                    url, 
                    request, 
                    NaverTokenRespDto.class
            );
            
            log.info("Access Token 발급 응답: {}", objectMapper.writeValueAsString(response.getBody()));
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Access Token 발급 실패", e);
            return null;
        }
    }
    
    /**
     * 네이버 사용자 프로필 조회
     */
    private NaverProfileRespDto getUserProfile(String accessToken) {
        try {
            log.info("=== 프로필 조회 요청 ===");
            
            String url = "https://openapi.naver.com/v1/nid/me";
            
            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            // API 호출
            ResponseEntity<NaverProfileRespDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    NaverProfileRespDto.class
            );
            
            log.info("프로필 조회 응답: {}", objectMapper.writeValueAsString(response.getBody()));
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("프로필 조회 실패", e);
            return null;
        }
    }
}