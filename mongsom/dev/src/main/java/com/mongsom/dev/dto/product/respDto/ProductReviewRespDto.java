package com.mongsom.dev.dto.product.respDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.mongsom.dev.entity.UserReview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewRespDto {
    
    private List<ReviewItemDto> items;
    private PaginationDto pagination;
    
    public static ProductReviewRespDto from(List<ReviewItemDto> items, Page<UserReview> page) {
        return ProductReviewRespDto.builder()
                .items(items)
                .pagination(PaginationDto.from(page))
                .build();
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewItemDto {
        private Integer reviewId;
        private String userId;
        private String userName;
        private Integer reviewRating;
        private String reviewContent;
        private LocalDateTime createdAt;
        private List<String> reviewImgUrls;
        
        public static ReviewItemDto from(UserReview review, String userName, List<String> reviewImgUrls) {
            return ReviewItemDto.builder()
                    .reviewId(review.getReviewId())
                    .userId(review.getUser().getUserId())
                    .userName(userName)
                    .reviewRating(review.getReviewRating())
                    .reviewContent(review.getReviewContent())
                    .createdAt(review.getCreatedAt())
                    .reviewImgUrls(reviewImgUrls)
                    .build();
        }
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationDto {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
        
        public static PaginationDto from(Page<?> page) {
            return PaginationDto.builder()
                    .currentPage(page.getNumber() + 1)
                    .totalPage(page.getTotalPages())
                    .size(page.getSize())
                    .hasNext(page.hasNext())
                    .build();
        }
    }
}