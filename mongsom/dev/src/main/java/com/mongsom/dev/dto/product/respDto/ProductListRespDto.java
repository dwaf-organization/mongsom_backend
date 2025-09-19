package com.mongsom.dev.dto.product.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListRespDto {
	 
    private List<ProductItemDto> items;
    private PaginationDto pagination;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductItemDto {
        private Integer productId;
        private String name;
        private Integer price;
        private Integer discountPer;
        private Integer discountPrice;
        private List<String> productImgUrls;
        
        public static ProductItemDto from(com.mongsom.dev.entity.Product product, List<String> productImgUrls) {
            return ProductItemDto.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .discountPer(product.getDiscountPer())
                    .discountPrice(product.getDiscountPrice())
                    .productImgUrls(productImgUrls != null ? productImgUrls : List.of())
                    .build();
        }
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationDto {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
        
        public static PaginationDto from(org.springframework.data.domain.Page<?> page) {
            return PaginationDto.builder()
                    .currentPage(page.getNumber() + 1)
                    .totalPage(page.getTotalPages())
                    .size(page.getSize())
                    .hasNext(page.hasNext())
                    .build();
        }
    }
    
    public static ProductListRespDto from(org.springframework.data.domain.Page<com.mongsom.dev.entity.Product> productPage, 
                                          Map<Integer, List<String>> productImgMap) {
        List<ProductItemDto> items = productPage.getContent().stream()
                .map(product -> {
                    List<String> productImgUrls = productImgMap.getOrDefault(product.getProductId(), List.of());
                    return ProductItemDto.from(product, productImgUrls);
                })
                .toList();
        
        PaginationDto pagination = PaginationDto.from(productPage);
        
        return ProductListRespDto.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }
}