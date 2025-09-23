package com.mongsom.dev.dto.auth.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateUserReqDto {
    
    @NotNull(message = "사용자 코드는 필수 값입니다.")
    private Long userCode;
    
    private String password;
    
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    
    @NotBlank(message = "우편번호는 필수 입력 값입니다.")
    private String zipCode;
    
    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
    
    @NotBlank(message = "상세주소는 필수 입력 값입니다.")
    private String address2;
    
    @NotBlank(message = "핸드폰 번호는 필수 입력 값입니다.")
    private String phone;
    
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    private String birth;
}