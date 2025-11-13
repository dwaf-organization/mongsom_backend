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
public class AdminOrderListRespDto {
    
    // order_item 테이블 정보
    private Integer orderId;
    private Long userCode;
    private String receivedUserName;
    private String receivedUserPhone;  // 새로 추가된 필드
    private Integer finalPrice;
    private String deliveryStatus;
    private String deliveryCom;
    private String invoiceNum;
    private Integer changeState;
    private LocalDateTime paymentAt;

    // order_detail + product + product_img 정보
    private List<OrderDetailDto> orderDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailDto {
        private Integer orderDetailId;
        private Integer productId;
        private String productName;        // product 테이블에서
        private List<String> productImgUrls; // product_img 테이블에서
    }
}