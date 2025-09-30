package com.mongsom.dev.service;

import java.util.Optional;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.reqDto.KakaoLoginCheckReqDto;
import com.mongsom.dev.dto.auth.respDto.KakaoUserInfoRespDto;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    
    /**
     * 카카오 Access Token으로 사용자 정보 조회
     * @param accessToken 카카오 Access Token
     * @return 카카오 사용자 정보
     */
    public RespDto<KakaoUserInfoRespDto> getKakaoUserInfo(String accessToken) {
        try {
            log.info("=== 카카오 사용자 정보 조회 시작 ===");
            log.info("Access Token: {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
            
            // 1. 카카오 사용자 정보 조회 API 엔드포인트
            String url = "https://kapi.kakao.com/v2/user/me";
            
            // 2. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
            
            // 3. HTTP 요청 생성
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            // 4. 카카오 API 호출
            ResponseEntity<KakaoUserInfoRespDto> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    KakaoUserInfoRespDto.class
            );
            
            // 5. 응답 로깅
            log.info("=== 카카오 사용자 정보 조회 성공 ===");
            log.info("Status Code: {}", response.getStatusCode());
            log.info("Response: {}", objectMapper.writeValueAsString(response.getBody()));
            
            KakaoUserInfoRespDto userInfo = response.getBody();
            
            if (userInfo != null) {
                log.info("회원번호: {}", userInfo.getId());
                if (userInfo.getKakaoAccount() != null) {
                    log.info("이메일: {}", userInfo.getKakaoAccount().getEmail());
                    if (userInfo.getKakaoAccount().getProfile() != null) {
                        log.info("닉네임: {}", userInfo.getKakaoAccount().getProfile().getNickname());
                    }
                }
            }
            
            return RespDto.<KakaoUserInfoRespDto>builder()
                    .code(1)
                    .data(userInfo)
                    .build();
            
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패", e);
            
            return RespDto.<KakaoUserInfoRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    // 이메일과 닉네임으로 기존 회원 여부 확인
    public RespDto<Long> checkKakaoLogin(KakaoLoginCheckReqDto reqDto) {
        try {
            log.info("=== 카카오 로그인 체크 시작 ===");
            log.info("Email: {}, Nickname: {}", reqDto.getEmail(), reqDto.getNickname());
            
            // 이메일과 닉네임으로 사용자 조회
            Optional<User> userOpt = userRepository.findByEmailAndNickname(
                    reqDto.getEmail(), 
                    reqDto.getNickname()
            );
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Long userCode = user.getUserCode();
                
                log.info("=== 기존 회원 확인 ===");
                log.info("UserCode: {}, Email: {}, Name: {}", 
                        userCode, user.getEmail(), user.getName());
                
                return RespDto.<Long>builder()
                        .code(1)
                        .data(userCode)
                        .build();
            } else {
                log.info("=== 신규 회원 - 일치하는 사용자 없음 ===");
                
                return RespDto.<Long>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("카카오 로그인 체크 실패 - email: {}, nickname: {}, error: {}", 
                    reqDto.getEmail(), reqDto.getNickname(), e.getMessage(), e);
            
            return RespDto.<Long>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
}