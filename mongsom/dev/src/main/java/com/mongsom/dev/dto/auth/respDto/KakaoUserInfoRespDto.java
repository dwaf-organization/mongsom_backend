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
public class KakaoUserInfoRespDto {
    
    private Long id;  // 회원번호
    
    @JsonProperty("connected_at")
    private String connectedAt;  // 서비스 연결 완료 시각
    
    private KakaoAccount kakaoAccount;  // 카카오 계정 정보
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount {
        
        private Boolean profileNicknameNeedsAgreement;
        private Boolean profileImageNeedsAgreement;
        
        private Profile profile;  // 프로필 정보
        
        private Boolean hasEmail;
        private Boolean emailNeedsAgreement;
        private Boolean isEmailValid;
        private Boolean isEmailVerified;
        private String email;  // 이메일
        
        private Boolean hasPhoneNumber;
        private Boolean phoneNumberNeedsAgreement;
        private String phoneNumber;  // 전화번호
        
        private Boolean hasAgeRange;
        private Boolean ageRangeNeedsAgreement;
        private String ageRange;  // 연령대
        
        private Boolean hasBirthday;
        private Boolean birthdayNeedsAgreement;
        private String birthday;  // 생일 (MMDD)
        
        private Boolean hasGender;
        private Boolean genderNeedsAgreement;
        private String gender;  // 성별
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Profile {
            private String nickname;  // 닉네임
            private String thumbnailImageUrl;  // 프로필 미리보기 이미지
            private String profileImageUrl;  // 프로필 사진
            private Boolean isDefaultImage;  // 기본 프로필 이미지 여부
        }
    }
}