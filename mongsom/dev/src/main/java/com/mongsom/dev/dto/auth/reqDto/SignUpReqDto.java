package com.mongsom.dev.dto.auth.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpReqDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String userId;
	
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;
    
    @NotBlank(message = "우편번호는 필수 입력 값입니다.")
    private String zipCode;
    
    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String address;
    
    @NotBlank(message = "상세 주소는 필수 입력 값입니다.")
    private String address2;
    
    @NotBlank(message = "핸드폰 번호는 필수 입력 값입니다.")
    private String phone;
    
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    
    private String birth;

    private boolean agreeMain;

    private boolean agreeShopping;

    private boolean agreeSms;
    
    private boolean agreeEmail;
    
    @NotBlank(message = "생성자는 필수 입력 값입니다.")
    private String provider;

    // 연호 수정
    private String code;
}
