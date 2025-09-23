package com.mongsom.dev.dto.admin.product.reqDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDeliveryUpdateReqDto {
    
    @NotNull(message = "주문번호는 필수입니다.")
    private Integer orderId;
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Long userCode;
    
    private String deliveryStatus;
    private String deliveryCom;
    private String invoiceNum;
}