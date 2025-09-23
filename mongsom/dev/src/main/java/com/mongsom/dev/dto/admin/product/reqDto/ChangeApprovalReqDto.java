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
public class ChangeApprovalReqDto {
    
    @NotNull(message = "교환/반품 ID는 필수입니다.")
    private Integer changeId;
    
    @NotNull(message = "승인 상태는 필수입니다.")
    private Integer approvalStatus;  // 0=승인대기, 1=승인, 2=반려
}