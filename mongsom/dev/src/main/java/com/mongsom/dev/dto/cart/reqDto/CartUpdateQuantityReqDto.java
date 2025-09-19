package com.mongsom.dev.dto.cart.reqDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class CartUpdateQuantityReqDto {
    
    @NotNull(message = "사용자 코드는 필수입니다")
    private Integer userCode;
    
    @NotNull(message = "상품 ID는 필수입니다")
    private Integer productId;
    
    private Integer optId;
    
    @NotNull(message = "수량은 필수입니다")
    @Positive(message = "수량은 1 이상이어야 합니다")
    private Integer quantity;
}