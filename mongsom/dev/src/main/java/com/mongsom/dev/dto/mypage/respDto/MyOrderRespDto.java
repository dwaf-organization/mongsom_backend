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
public class MyOrderRespDto {
    
    private Integer orderId;
    private LocalDateTime paymentAt;
    private String deliveryStatus;
    private Integer finalPrice;
    private List<MyOrderDetailDto> details;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyOrderDetailDto {
        private Integer productId;
        private String productName;
        private Integer optId;
        private String optName;
        private Integer price;
        private List<String> productImgUrls;
    }
}