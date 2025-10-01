package com.mongsom.dev.dto.admin.inquiry.respDto;

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
public class AdminInquiryListRespDto {
    
    private List<InquiryInfo> inquiries;
    private Pagination pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InquiryInfo {
        private Integer inquiryId;
        private String category;
        private String phone;
        private String email;
        private String companyName;
        private String price;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
    }
}