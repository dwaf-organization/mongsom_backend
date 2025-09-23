package com.mongsom.dev.dto.review.reqDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateReqDto {
    
    @NotNull(message = "리뷰 ID는 필수입니다.")
    private Integer reviewId;
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Long userCode;
    
    @NotNull(message = "리뷰 평점은 필수입니다.")
    @Min(value = 1, message = "리뷰 평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "리뷰 평점은 5점 이하여야 합니다.")
    private Integer reviewRating;
    
    @NotBlank(message = "리뷰 내용은 필수입니다.")
    private String reviewContent;
    
    private List<String> reviewImgUrls;
}