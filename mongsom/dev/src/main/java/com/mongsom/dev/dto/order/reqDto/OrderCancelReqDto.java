package com.mongsom.dev.dto.order.reqDto;

import jakarta.validation.constraints.NotNull;
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
public class OrderCancelReqDto {
    
    @NotNull(message = "주문 ID는 필수입니다")
    private Integer orderId;
    
    @NotNull(message = "사용자 코드는 필수입니다")
    private Long userCode;
    
    @NotNull(message = "주문 상세 ID는 필수입니다")
    private Integer orderDetailId;
}