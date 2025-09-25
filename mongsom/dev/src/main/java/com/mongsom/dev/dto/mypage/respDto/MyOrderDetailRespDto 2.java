package com.mongsom.dev.dto.mypage.respDto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderDetailRespDto {
    
    // 기본 주문 정보 (기존과 동일)
    private Integer orderId;
    private LocalDateTime paymentAt;
    private String deliveryStatus;
    private Integer finalPrice;
    
    // 추가 주문 정보 (order_item에서)
    private Integer userCode;
    private String receivedUserName;
    private String receivedUserPhone;
    private String receivedUserZipCode;
    private String receivedUserAddress;
    private String receivedUserAddress2;
    private String message;
    private Integer changeState;
    
    // 결제 정보 (payments에서)
    private String paymentMethod;
    private Integer paymentAmount;
    private String paymentStatus;
    private String pgProvider;
    
    // 주문 상품 상세 목록 (기존과 동일하지만 추가 필드들 포함)
    private List<MyOrderDetailItemDto> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyOrderDetailItemDto {
    	private Integer orderDetailId;
        private Integer productId;
        private String productName;
        private Integer optId;
        private String optName;
        private Integer changeStatus;
        private List<String> productImgUrls;
        private Integer quantity;    // order_detail에서 추가
        private Integer price;       // order_detail에서 추가  
        private Integer orderStatus; // order_detail에서 추가
    }
}