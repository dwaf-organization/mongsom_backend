package com.mongsom.dev.controller.admin;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.inquiry.respDto.AdminInquiryListRespDto;
import com.mongsom.dev.service.admin.AdminInquiryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin/inquiry")
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryController {
    
    private final AdminInquiryService adminInquiryService;
    
    /**
     * 관리자 견적문의 목록 조회 (페이지네이션)
     * GET /api/v1/admin/inquiry/list/{page}/{size}
     * 
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @return 견적문의 목록 및 페이지 정보
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseEntity<RespDto<AdminInquiryListRespDto>> getInquiryList(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        
        log.info("관리자 견적문의 목록 조회 요청 - page: {}, size: {}", page, size);
        
        RespDto<AdminInquiryListRespDto> response = adminInquiryService.getInquiryListWithPagination(page, size);
        
        return ResponseEntity.ok(response);
    }
}