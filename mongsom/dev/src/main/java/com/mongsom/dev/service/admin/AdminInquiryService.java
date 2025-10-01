package com.mongsom.dev.service.admin;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.inquiry.respDto.AdminInquiryListRespDto;
import com.mongsom.dev.entity.Inquiry;
import com.mongsom.dev.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryService {
    
    private final InquiryRepository inquiryRepository;
    
    /**
     * 관리자 견적문의 목록 조회 (페이지네이션)
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @return 견적문의 목록 및 페이지 정보
     */
    public RespDto<AdminInquiryListRespDto> getInquiryListWithPagination(Integer page, Integer size) {
        try {
            log.info("=== 관리자 견적문의 목록 조회 시작 - page: {}, size: {} ===", page, size);
            
            // 페이지 번호는 1부터 시작하지만 Spring Data JPA는 0부터 시작
            Pageable pageable = PageRequest.of(page - 1, size);
            
            // 견적문의 페이지네이션 조회
            Page<Inquiry> inquiryPage = inquiryRepository.findAllWithPagination(pageable);
            
            // Inquiry 엔티티를 InquiryInfo DTO로 변환
            List<AdminInquiryListRespDto.InquiryInfo> inquiryList = inquiryPage.getContent().stream()
                    .map(inquiry -> AdminInquiryListRespDto.InquiryInfo.builder()
                            .inquiryId(inquiry.getInquiryId())
                            .category(inquiry.getCategory())
                            .phone(inquiry.getPhone())
                            .email(inquiry.getEmail())
                            .companyName(inquiry.getCompanyName())
                            .price(inquiry.getPrice())
                            .build())
                    .collect(Collectors.toList());
            
            // 페이지네이션 정보 생성
            AdminInquiryListRespDto.Pagination pagination = AdminInquiryListRespDto.Pagination.builder()
                    .currentPage(page)
                    .totalPage(inquiryPage.getTotalPages())
                    .size(size)
                    .hasNext(inquiryPage.hasNext())
                    .build();
            
            // 최종 응답 DTO 생성
            AdminInquiryListRespDto responseDto = AdminInquiryListRespDto.builder()
                    .inquiries(inquiryList)
                    .pagination(pagination)
                    .build();
            
            log.info("=== 견적문의 목록 조회 완료 - 조회된 개수: {}, 전체 페이지: {}, 다음 페이지 존재: {} ===", 
                    inquiryList.size(), inquiryPage.getTotalPages(), inquiryPage.hasNext());
            
            return RespDto.<AdminInquiryListRespDto>builder()
                    .code(1)
                    .data(responseDto)
                    .build();
            
        } catch (Exception e) {
            log.error("견적문의 목록 조회 실패 - page: {}, size: {}, error: {}", 
                    page, size, e.getMessage(), e);
            
            return RespDto.<AdminInquiryListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
}