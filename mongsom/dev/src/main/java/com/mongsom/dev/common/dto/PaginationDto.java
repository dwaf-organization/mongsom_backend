package com.mongsom.dev.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto {
    
    private Integer currentPage;  // 현재 페이지 (1부터 시작)
    private Integer totalPage;    // 전체 페이지 수
    private Integer size;         // 현재 페이지 항목 수
    private Boolean hasNext;      // 다음 페이지 여부
}