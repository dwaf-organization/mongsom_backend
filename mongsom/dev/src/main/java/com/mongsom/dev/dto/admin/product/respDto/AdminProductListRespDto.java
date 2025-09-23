package com.mongsom.dev.dto.admin.product.respDto;

import java.util.List;

import com.mongsom.dev.common.dto.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListRespDto {
    
    // 상품 목록
    private List<AdminProductSelectRespDto> products;
    
    // 페이징 정보
    private PaginationDto pagination;
}