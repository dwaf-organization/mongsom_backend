package com.mongsom.dev.dto.auth.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindPwReqDto {
    
    @NotBlank(message = "사용자 ID는 필수 입력 값입니다.")
    private String userId;
    
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
}