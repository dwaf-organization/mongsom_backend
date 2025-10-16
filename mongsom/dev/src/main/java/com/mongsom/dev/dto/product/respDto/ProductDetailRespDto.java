package com.mongsom.dev.dto.product.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailRespDto {
    
    private Integer productId;
    private String name;
    private String contents;
    private Integer price;
    private Integer salesMargin;
    private Integer discountPer;
    private Integer discountPrice;
    private Integer deleteStatus;
    private List<String> productImgUrl;
    private List<ProductOptionDto> productOptions;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductOptionDto {
        private Integer optId;
        private String optName;
        
        public static ProductOptionDto from(com.mongsom.dev.entity.ProductOption productOption) {
            return ProductOptionDto.builder()
                    .optId(productOption.getOptId())
                    .optName(productOption.getOptName())
                    .build();
        }
    }
    
    public static ProductDetailRespDto from(com.mongsom.dev.entity.Product product) {
        List<String> imgUrls = product.getProductImages()
                .stream()
                .map(productImg -> productImg.getProductImgUrl())
                .collect(Collectors.toList());
        
        List<ProductOptionDto> options = product.getProductOptions()
                .stream()
                .map(ProductOptionDto::from)
                .collect(Collectors.toList());
        
        return ProductDetailRespDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .contents(product.getContents())
                .price(product.getPrice())
                .salesMargin(product.getSalesMargin())
                .discountPer(product.getDiscountPer())
                .discountPrice(product.getDiscountPrice())
                .deleteStatus(product.getDeleteStatus())
                .productImgUrl(imgUrls)
                .productOptions(options)
                .build();
    }
}