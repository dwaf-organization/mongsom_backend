package com.mongsom.dev.dto.auth.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginReqDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
}
