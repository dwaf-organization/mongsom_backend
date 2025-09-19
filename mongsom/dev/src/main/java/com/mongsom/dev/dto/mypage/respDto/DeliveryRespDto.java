package com.mongsom.dev.dto.mypage.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRespDto {
    
    private String deliveryCom;     // 택배회사
    private String invoiceNum;      // 송장번호
}