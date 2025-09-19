package com.mongsom.dev.dto.admin.product.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductRegistReqDto {
    
    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String name;
    
    private String contents;
    
    private Integer premium; // 0=일반상품, 1=프리미엄상품 (기본값 0)
    
    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;
    
    private Integer salesMargin;
    
    private Integer discountPer;
    
    private Integer discountPrice;
    
    private Integer deliveryPrice = 3000; // 기본값 3000원
    
    @NotEmpty(message = "상품 이미지는 최소 1개 이상 필요합니다.")
    private List<String> productImgUrls;
    
    @NotEmpty(message = "상품 옵션은 최소 1개 이상 필요합니다.")
    private List<String> optNames;
    
    // Getter가 없는 필드들에 대한 기본값 설정
    public Integer getPremium() {
        return premium != null ? premium : 0;
    }
    
    public Integer getDeliveryPrice() {
        return deliveryPrice != null ? deliveryPrice : 3000;
    }
}