package com.mongsom.dev.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginRespDto {
    
    private Long userCode;  // 기존 회원인 경우 userCode
    private NaverProfileRespDto profile;  // 신규 회원인 경우 프로필 정보
}