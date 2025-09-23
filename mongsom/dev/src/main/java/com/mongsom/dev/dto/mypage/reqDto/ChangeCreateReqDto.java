package com.mongsom.dev.dto.mypage.reqDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ChangeCreateReqDto {
    
    @NotNull(message = "주문 상품 ID는 필수입니다.")
    private Integer orderDetailId;
    
    @NotNull(message = "주문 ID는 필수입니다.")
    private Integer orderId;
    
    @NotNull(message = "사용자 코드는 필수입니다.")
    private Long userCode;
    
    @NotNull(message = "교환/반품 상태는 필수입니다.")
    @Min(value = 1, message = "교환/반품 상태는 1(교환) 또는 2(반품)이어야 합니다.")
    @Max(value = 2, message = "교환/반품 상태는 1(교환) 또는 2(반품)이어야 합니다.")
    private Integer changeStatus; // 1=교환, 2=반품
    
    @NotBlank(message = "신청 사유는 필수입니다.")
    private String contents;
}