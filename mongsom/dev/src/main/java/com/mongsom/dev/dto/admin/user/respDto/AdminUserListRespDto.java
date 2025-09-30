package com.mongsom.dev.dto.admin.user.respDto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserListRespDto {
    
    private List<UserInfo> users;
    private Pagination pagination;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long userCode;
        private String userId;
        private String name;
        private String email;
        private String phone;
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