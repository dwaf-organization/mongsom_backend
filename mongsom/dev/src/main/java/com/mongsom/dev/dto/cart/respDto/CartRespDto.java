package com.mongsom.dev.dto.cart.respDto;

import java.util.List;

import com.mongsom.dev.entity.Cart;

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
public class CartRespDto {
    
    private List<CartItemDto> items;
    
    public static CartRespDto from(List<CartItemDto> items) {
        return CartRespDto.builder()
                .items(items)
                .build();
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartItemDto {
        private Integer cartId;
        private Integer optId;
        private String optName;
        private Integer productId;
        private String productName;
        private Integer price;
        private Integer discountPer;
        private Integer discountPrice;
        private Integer quantity;
        private Integer checkStatus;
        private List<String> productImgUrl;
        
        public static CartItemDto from(Cart cart, List<String> productImgUrls) {
            String optName = null;
            Integer optId = null;
            
            if (cart.getProductOption() != null && 
                cart.getProductOption().getProductId().equals(cart.getProductId())) {
                optId = cart.getProductOption().getOptId();
                optName = cart.getProductOption().getOptName();
            }
            
            return CartItemDto.builder()
                    .cartId(cart.getCartId())
                    .optId(optId)
                    .optName(optName)
                    .productId(cart.getProduct().getProductId())
                    .productName(cart.getProduct().getName())
                    .price(cart.getProduct().getPrice())
                    .discountPer(cart.getProduct().getDiscountPer())
                    .discountPrice(cart.getProduct().getDiscountPrice())
                    .quantity(cart.getQuantity())
                    .checkStatus(cart.getCheckStatus())
                    .productImgUrl(productImgUrls != null ? productImgUrls : List.of())
                    .build();
        }
    }
}