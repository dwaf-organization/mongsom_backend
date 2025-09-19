package com.mongsom.dev.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeCreateReqDto;
import com.mongsom.dev.service.admin.AdminNoticeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin/notice")
@RequiredArgsConstructor
@Slf4j
public class AdminNoticeController {
    
    private final AdminNoticeService adminNoticeService;
    
    @PostMapping("/create")
    public ResponseEntity<RespDto<Boolean>> createNotice(
            @Valid @RequestBody NoticeCreateReqDto reqDto) {
        
        log.info("공지사항 등록 요청 - title: {}", reqDto.getTitle());
        
        RespDto<Boolean> response = adminNoticeService.createNotice(reqDto);
        return ResponseEntity.ok(response);
    }
}