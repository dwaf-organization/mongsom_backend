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
public class AdminOrderListReqDto {
    
    @NotNull(message = "시작일은 필수입니다.")
    private String startDate;  // YYYY-MM-DD 형식
    
    @NotNull(message = "종료일은 필수입니다.")
    private String endDate;    // YYYY-MM-DD 형식
    
    private String orderId;    // null 허용, 주문번호 포함 검색
}