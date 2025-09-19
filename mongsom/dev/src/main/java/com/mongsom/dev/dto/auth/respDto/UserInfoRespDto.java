package com.mongsom.dev.dto.auth.respDto;

import com.mongsom.dev.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoRespDto {

    private String userId;
    private String password;
    private String name;
    private String zipCode;
    private String address;
    private String address2;
    private String phone;
    private String email;
    private String birth;
    
    // User Entity에서 DTO로 변환하는 정적 메서드
    public static UserInfoRespDto from(User user) {
        return UserInfoRespDto.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .name(user.getName())
                .zipCode(user.getZipCode())
                .address(user.getAddress())
                .address2(user.getAddress2())
                .phone(user.getPhone())
                .email(user.getEmail())
                .birth(user.getBirth())
                .build();
    }
}
