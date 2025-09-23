package com.mongsom.dev.dto.admin.product.reqDto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductUpdateReqDto {
    
    @NotBlank(message = "상품명은 필수입니다.")
    private String name;
    
    private String contents;  // 상품 설명 (선택적)
    
    @NotNull(message = "프리미엄 여부는 필수입니다.")
    private Integer premium;  // 0=일반, 1=프리미엄
    
    @NotNull(message = "가격은 필수입니다.")
    private Integer price;
    
    private Integer salesMargin;
    
    private Integer discountPer;
    
    private Integer discountPrice;
    
    private Integer deliveryPrice;
    
    @NotEmpty(message = "상품 이미지는 최소 1개 이상 필요합니다.")
    private List<String> productImgUrls;
    
    @NotEmpty(message = "상품 옵션은 최소 1개 이상 필요합니다.")
    private List<ProductOptionDto> options;  // optNames에서 options로 변경
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOptionDto {
        private Integer optId;    // null이면 새로 생성, 값이 있으면 수정
        
        @NotBlank(message = "옵션명은 필수입니다.")
        private String optName;
    }
}