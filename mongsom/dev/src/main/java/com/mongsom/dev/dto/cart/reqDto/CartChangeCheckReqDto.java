package com.mongsom.dev.dto.cart.reqDto;

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
public class CartChangeCheckReqDto {
    
    @NotNull(message = "사용자 코드는 필수입니다")
    private Long userCode;
    
    @NotNull(message = "상품 ID는 필수입니다")
    private Integer productId;
    
    private Integer optId;
}