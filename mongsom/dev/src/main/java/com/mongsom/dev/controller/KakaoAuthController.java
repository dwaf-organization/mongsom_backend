package com.mongsom.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.reqDto.KakaoLoginCheckReqDto;
import com.mongsom.dev.dto.auth.respDto.KakaoUserInfoRespDto;
import com.mongsom.dev.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {
    
    private final KakaoAuthService kakaoAuthService;
    
    // 카카오 사용자 정보 조회
    @GetMapping("/user-info")
    public ResponseEntity<RespDto<KakaoUserInfoRespDto>> getKakaoUserInfo(
            @RequestHeader("Authorization") String authorization) {
        
        log.info("카카오 사용자 정보 조회 요청");
        
        // "Bearer " 접두사 제거
        String accessToken = authorization.replace("Bearer ", "");
        
        RespDto<KakaoUserInfoRespDto> response = kakaoAuthService.getKakaoUserInfo(accessToken);
        
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
    // 카카오 소셜 로그인 체크
    @GetMapping("/login")
    public ResponseEntity<RespDto<Long>> checkKakaoLogin(
            @RequestParam("email") String email,
            @RequestParam("nickname") String nickname) {
        
        log.info("카카오 로그인 체크 요청 - email: {}, nickname: {}", email, nickname);
        
        KakaoLoginCheckReqDto reqDto = KakaoLoginCheckReqDto.builder()
                .email(email)
                .nickname(nickname)
                .build();
        
        RespDto<Long> response = kakaoAuthService.checkKakaoLogin(reqDto);
        
        return ResponseEntity.ok(response);
    }
}