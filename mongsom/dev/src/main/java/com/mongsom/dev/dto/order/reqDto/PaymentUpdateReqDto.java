package com.mongsom.dev.dto.order.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUpdateReqDto {
    
    @NotNull(message = "주문 ID는 필수입니다.")
    private Integer orderId;
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Long userCode;
    
    @NotBlank(message = "결제 수단은 필수입니다.")
    private String paymentMethod;
    
    private String paymentStatus;
    
    private String paymentKey;
    
    private String pgProvider;
}