package com.mongsom.dev.dto.cart.reqDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAllCheckReqDto {
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Integer userCode;
    
    @NotNull(message = "전체 체크 상태는 필수입니다.")
    @Min(value = 0, message = "체크 상태는 0(해제) 또는 1(선택)이어야 합니다.")
    @Max(value = 1, message = "체크 상태는 0(해제) 또는 1(선택)이어야 합니다.")
    private Integer allCheckStatus;
}