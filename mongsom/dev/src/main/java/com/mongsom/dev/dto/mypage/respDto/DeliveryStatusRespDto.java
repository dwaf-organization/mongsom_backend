package com.mongsom.dev.dto.mypage.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusRespDto {
    
    private Integer paymentCompleted;    // 결제완료
    private Integer preparing;           // 상품준비중
    private Integer shipping;            // 배송중
    private Integer delivered;           // 배송완료
}