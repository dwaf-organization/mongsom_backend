package com.mongsom.dev.dto.auth.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginCheckReqDto {
    
    private String email;     // 카카오 이메일
    private String nickname;  // 카카오 닉네임
}