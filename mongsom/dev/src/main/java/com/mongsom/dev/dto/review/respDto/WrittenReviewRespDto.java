package com.mongsom.dev.dto.review.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrittenReviewRespDto {
    
    private List<WrittenReviewItemDto> items;
    private PaginationDto pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WrittenReviewItemDto {
        private Integer orderDetailId;
        private Integer optId;
        private Integer productId;
        private Integer reviewStatus;
        private String optName;
        private String productName;
        private List<String> productImgUrls;
        
        // 리뷰 정보
        private Integer reviewId;
        private Integer reviewRating;
        private String reviewContent;
        private LocalDateTime reviewCreatedAt;
        private List<String> reviewImgUrls;
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