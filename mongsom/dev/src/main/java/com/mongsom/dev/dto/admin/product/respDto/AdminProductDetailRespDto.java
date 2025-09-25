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
public class AdminProductDetailRespDto {
    
    // Product 기본 정보
    private Integer productId;
    private String name;
    private String contents;
    private Integer premium;         // 0=일반, 1=프리미엄
    private Integer price;
    private Integer salesMargin;
    private Integer discountPer;
    private Integer discountPrice;
    private Integer deliveryPrice;
    private Integer deleteStatus;
    
    // ProductOption 옵션명 리스트
    private List<ProductOptionDto> options;
    
    // ProductImg URL 리스트  
    private List<String> productImgUrls;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOptionDto {
        private Integer optId;
        private String optName;
    }
}