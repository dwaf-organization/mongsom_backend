package com.mongsom.dev.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.user.reqDto.AdminLoginReqDto;
import com.mongsom.dev.dto.admin.user.respDto.AdminUserListRespDto;
import com.mongsom.dev.service.admin.AdminUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {
    
    private final AdminUserService adminUserService;
    
    // 관리자 회원 정보 조회
    @GetMapping("/user/list/{page}/{size}")
    public ResponseEntity<RespDto<AdminUserListRespDto>> getUserList(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        
        log.info("관리자 회원 정보 조회 요청 - page: {}, size: {}", page, size);
        
        RespDto<AdminUserListRespDto> response = adminUserService.getUserList(page, size);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 관리자 로그인
     * POST /api/v1/admin/auth/login
     * 
     * @param reqDto 아이디, 비밀번호
     * @return 로그인 성공 시 관리자 정보
     */
    @PostMapping("/auth/login")
    public ResponseEntity<RespDto<Long>> adminLogin(
            @Valid @RequestBody AdminLoginReqDto reqDto) {
        
        log.info("관리자 로그인 요청 - userId: {}", reqDto.getUserId());
        
        RespDto<Long> response = adminUserService.adminLogin(reqDto);
        
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
        
        return ResponseEntity.status(status).body(response);
    }
    
}