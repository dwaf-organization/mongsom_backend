package com.mongsom.dev.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.inquiry.reqDto.InquiryCreateReqDto;
import com.mongsom.dev.entity.Inquiry;
import com.mongsom.dev.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InquiryService {
    
    private final InquiryRepository inquiryRepository;
    
    /**
     * 견적문의 등록
     * @param reqDto 견적문의 정보
     * @return 등록된 견적문의 ID
     */
    @Transactional
    public RespDto<Integer> createInquiry(InquiryCreateReqDto reqDto) {
        try {
            log.info("=== 견적문의 등록 시작 - category: {}, companyName: {} ===", 
                    reqDto.getCategory(), reqDto.getCompanyName());
            
            Inquiry inquiry = Inquiry.builder()
                    .category(reqDto.getCategory())
                    .phone(reqDto.getPhone())
                    .email(reqDto.getEmail())
                    .companyName(reqDto.getCompanyName())
                    .price(reqDto.getPrice())
                    .build();
            
            Inquiry savedInquiry = inquiryRepository.save(inquiry);
            
            log.info("=== 견적문의 등록 완료 - inquiryId: {} ===", savedInquiry.getInquiryId());
            
            return RespDto.<Integer>builder()
                    .code(1)
                    .data(savedInquiry.getInquiryId())
                    .build();
            
        } catch (Exception e) {
            log.error("견적문의 등록 실패 - category: {}, error: {}", 
                    reqDto.getCategory(), e.getMessage(), e);
            
            return RespDto.<Integer>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
}