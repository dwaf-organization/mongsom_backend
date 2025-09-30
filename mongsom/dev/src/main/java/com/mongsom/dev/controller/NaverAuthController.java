package com.mongsom.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.respDto.NaverLoginRespDto;
import com.mongsom.dev.service.NaverAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth/naver")
@RequiredArgsConstructor
@Slf4j
public class NaverAuthController {
    
    private final NaverAuthService naverAuthService;
    
    // 네이버 로그인 콜백 처리
    @GetMapping("/callback")
    public ResponseEntity<RespDto<NaverLoginRespDto>> naverCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        
        log.info("네이버 로그인 콜백 요청 - code: {}, state: {}", code, state);
        
        RespDto<NaverLoginRespDto> response = naverAuthService.getNaverProfile(code, state);
        
        HttpStatus status = response.getCode() >= 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
}