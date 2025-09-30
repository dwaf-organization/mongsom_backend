package com.mongsom.dev.dto.auth.respDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverProfileRespDto {
    
    @JsonProperty("resultcode")
    private String resultCode;
    
    private String message;
    
    private NaverProfile response;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NaverProfile {
        private String id;           // 동일인 식별 정보
        private String nickname;     // 사용자 별명
        private String name;         // 사용자 이름
        private String email;        // 사용자 메일 주소
        private String gender;       // 성별 (F: 여성, M: 남성, U: 확인불가)
        private String age;          // 사용자 연령대
        private String birthday;     // 사용자 생일 (MM-DD)
        
        @JsonProperty("profile_image")
        private String profileImage; // 사용자 프로필 사진 URL
        
        @JsonProperty("birthyear")
        private String birthYear;    // 출생연도
        
        private String mobile;       // 휴대전화번호
    }
}