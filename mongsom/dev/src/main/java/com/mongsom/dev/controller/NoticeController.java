package com.mongsom.dev.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.notice.respDto.NoticeDetailRespDto;
import com.mongsom.dev.dto.notice.respDto.NoticeRespDto;
import com.mongsom.dev.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {
    
    private final NoticeService noticeService;
    
    @GetMapping("/detail/{noticeId}")
    public ResponseEntity<RespDto<NoticeDetailRespDto>> getNoticeDetail(
            @PathVariable("noticeId") Integer noticeId) {
        
        log.info("공지사항 상세 조회 요청 - noticeId: {}", noticeId);
        
        RespDto<NoticeDetailRespDto> response = noticeService.getNoticeDetail(noticeId);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    @GetMapping("/list/{page}")
    public ResponseEntity<RespDto<NoticeRespDto>> getAllNotices(
            @PathVariable("page") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        log.info("공지사항 조회 요청 - page: {}, size: {}", page, size);
        
        // 페이지 번호 검증 (1-based를 0-based로 변환)
        if (page < 1) {
            page = 1;
        }
        
        // 사이즈 검증
        if (size < 1) {
            size = 10;
        }
        
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page - 1, size);
        
        RespDto<NoticeRespDto> response = noticeService.getAllNotices(pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    

    
}