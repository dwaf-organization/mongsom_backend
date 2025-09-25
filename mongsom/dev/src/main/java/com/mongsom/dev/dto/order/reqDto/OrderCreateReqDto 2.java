package com.mongsom.dev.dto.order.reqDto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class OrderCreateReqDto {
    
    // order_item 테이블 관련 필드
    @NotNull(message = "사용자 코드는 필수입니다")
    private Integer userCode;
    
    @NotNull(message = "받는 사람 이름은 필수입니다")
    private String receivedUserName;
    
    @NotNull(message = "받는 사람 전화번호는 필수입니다")
    private String receivedUserPhone;
    
    private String receivedUserZipCode;
    
    @NotNull(message = "받는 사람 주소는 필수입니다")
    private String receivedUserAddress;
    
    @NotNull(message = "받는 사람 상세주소는 필수입니다")
    private String receivedUserAddress2;
    
    private String message;
    
    @NotNull(message = "총 주문금액은 필수입니다")
    @Positive(message = "총 주문금액은 0보다 커야 합니다")
    private Integer totalPrice;
    
    @NotNull(message = "배송비는 필수입니다")
    private Integer deliveryPrice;
    
    @NotNull(message = "총 할인금액은 필수입니다")
    private Integer totalDiscountPrice;
    
    @NotNull(message = "최종 결제금액은 필수입니다")
    @Positive(message = "최종 결제금액은 0보다 커야 합니다")
    private Integer finalPrice;
    
    private LocalDateTime paymentAt;
    
    // payments 테이블 관련 필드
    @NotNull(message = "결제수단은 필수입니다")
    private String paymentMethod;
    
    @NotNull(message = "결제금액은 필수입니다")
    @Positive(message = "결제금액은 0보다 커야 합니다")
    private Integer paymentAmount;
    
    private String paymentStatus;
    
    private String paymentKey;
    
    private String pgProvider;
    
    // order_detail 테이블 관련 필드 (리스트로 여러 상품 처리)
    @NotEmpty(message = "주문 상품 목록은 필수입니다")
    @Valid
    private List<OrderDetailDto> orderDetails;
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        
        private Integer optId;  // null 가능 (옵션 없는 상품)
        
        @NotNull(message = "상품 ID는 필수입니다")
        private Integer productId;
        
        @NotNull(message = "수량은 필수입니다")
        @Positive(message = "수량은 1 이상이어야 합니다")
        private Integer quantity;
        
        @NotNull(message = "가격은 필수입니다")
        @Positive(message = "가격은 0보다 커야 합니다")
        private Integer price;
    }
}