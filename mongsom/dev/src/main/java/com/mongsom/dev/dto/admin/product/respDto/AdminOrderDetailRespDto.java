package com.mongsom.dev.dto.admin.product.respDto;

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
public class AdminOrderDetailRespDto {
    
    // 주문 기본 정보
    private Integer orderId;
    private LocalDateTime paymentAt;
    private String deliveryStatus;
    private Integer finalPrice;
    private Long userCode;
    private String receivedUserName;
    private String receivedUserPhone;
    private String receivedUserZipCode;
    private String receivedUserAddress;
    private String receivedUserAddress2;
    private String message;
    private Integer changeState;
    
    // 배송 정보 추가
    private String deliveryCom;
    private String invoiceNum;
    
    // 결제 정보
    private String paymentMethod;
    private Integer paymentAmount;
    private String paymentStatus;
    private String pgProvider;
    
    // 주문 상세 목록
    private List<OrderDetailDto> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailDto {
        private Integer orderDetailId;
        private Integer productId;
        private String productName;
        private Integer optId;
        private String optName;
        private Integer changeStatus;    // change_item 테이블의 change_status
        private List<String> productImgUrls;
        private Integer quantity;
        private Integer price;
        private Integer orderStatus;     // order_detail 테이블의 order_status
    }
}