package com.mongsom.dev.dto.admin.product.respDto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeProductListRespDto {
    
    private List<ChangeItemDto> changeItems;
    private PaginationDto pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeItemDto {
        // change_item 테이블
        private Integer changeId;
        private Integer orderDetailId;
        private Integer orderId;
        private Long userCode;
        private Integer changeStatus;
        private Integer approvalStatus;
        private String contents;
        
        // order_item 테이블 조인
        private LocalDateTime paymentAt;
        private String receivedUserName;
        private Integer finalPrice;
        
        // product 테이블 조인
        private String productName;
        
        // product_img 테이블 조인
        private List<String> productImgUrls;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
    }
}