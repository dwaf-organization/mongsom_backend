package com.mongsom.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.inquiry.reqDto.InquiryCreateReqDto;
import com.mongsom.dev.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/inquiry")
@RequiredArgsConstructor
@Slf4j
public class InquiryController {
    
    private final InquiryService inquiryService;
    
    /**
     * 견적문의 등록
     * POST /api/v1/inquiry/create
     * 
     * @param reqDto 견적문의 정보
     * @return 등록된 견적문의 ID
     */
    @PostMapping("/create")
    public ResponseEntity<RespDto<Integer>> createInquiry(
            @Valid @RequestBody InquiryCreateReqDto reqDto) {
        
        log.info("견적문의 등록 요청 - category: {}, companyName: {}", 
                reqDto.getCategory(), reqDto.getCompanyName());
        
        RespDto<Integer> response = inquiryService.createInquiry(reqDto);
        
        HttpStatus status = response.getCode() == 1 ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
}