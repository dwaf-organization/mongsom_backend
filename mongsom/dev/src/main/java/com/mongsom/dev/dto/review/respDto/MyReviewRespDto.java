package com.mongsom.dev.dto.review.respDto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewRespDto {
    
    private List<MyReviewItemDto> items;
    private PaginationDto pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyReviewItemDto {
        private Integer orderDetailId;
        private Integer optId;
        private Integer productId;
        private Integer reviewStatus;
        private String optName;
        private String productName;
        private List<String> productImgUrls;
        private LocalDateTime paymentAt;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
    }
}