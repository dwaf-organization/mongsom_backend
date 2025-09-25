package com.mongsom.dev.dto.mypage.reqDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDeleteReqDto {
    
    @NotNull(message = "주문 상품 ID는 필수입니다.")
    private Integer orderItemId;
    
    @NotNull(message = "주문 ID는 필수입니다.")
    private Integer orderId;
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Integer userCode;
}