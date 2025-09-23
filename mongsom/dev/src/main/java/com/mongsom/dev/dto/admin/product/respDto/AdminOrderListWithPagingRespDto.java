package com.mongsom.dev.dto.admin.product.respDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderListWithPagingRespDto {
    
    private List<AdminOrderListRespDto> orders;
    private PaginationDto pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private Integer currentPage;  // 현재 페이지 (1부터 시작)
        private Integer totalPage;    // 총 페이지 수
        private Integer size;         // 총 데이터 개수
        private Boolean hasNext;      // 다음 페이지 존재 여부
    }
}