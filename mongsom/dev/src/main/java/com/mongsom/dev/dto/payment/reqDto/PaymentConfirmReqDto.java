package com.mongsom.dev.dto.payment.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmReqDto {
    private String paymentKey;
    private String orderId;
    private Integer amount;
}