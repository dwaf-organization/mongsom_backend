package com.mongsom.dev.dto.inquiry.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryCreateReqDto {
    
    @NotBlank(message = "카테고리는 필수입니다.")
    private String category;
    
    @NotBlank(message = "전화번호는 필수입니다.")
    private String phone;
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "회사명은 필수입니다.")
    private String companyName;
    
    @NotBlank(message = "가격은 필수입니다.")
    private String price;
}