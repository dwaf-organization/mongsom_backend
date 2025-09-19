package com.mongsom.dev.dto.auth.respDto;

import com.mongsom.dev.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRespDto {
    private Long userCode;

    public LoginRespDto(User user) {
        this.userCode = user.getUserCode();
    }
}
