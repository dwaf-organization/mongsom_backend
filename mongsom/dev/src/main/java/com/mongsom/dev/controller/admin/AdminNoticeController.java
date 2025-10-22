package com.mongsom.dev.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeCreateReqDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeUpdateReqDto;
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
    
    // 공지사항 수정
    @PutMapping("/update/{noticeId}")
    public ResponseEntity<RespDto<Boolean>> updateNotice(
            @PathVariable("noticeId") Integer noticeId,
            @Valid @RequestBody NoticeUpdateReqDto reqDto) {
        
        log.info("공지사항 수정 요청 - noticeId: {}, title: {}", 
                noticeId, reqDto.getTitle());
        
        RespDto<Boolean> response = adminNoticeService.updateNotice(noticeId, reqDto);
        
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
    // 공지사항 삭제
    @DeleteMapping("/delete/{noticeId}")
    public ResponseEntity<RespDto<Boolean>> deleteNotice(
            @PathVariable("noticeId") Integer noticeId) {
        
        log.info("공지사항 삭제 요청 - noticeId: {}", noticeId);
        
        RespDto<Boolean> response = adminNoticeService.deleteNotice(noticeId);
        
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
}