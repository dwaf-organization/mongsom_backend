package com.mongsom.dev.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.reqDto.FindIdReqDto;
import com.mongsom.dev.dto.auth.reqDto.FindPwReqDto;
import com.mongsom.dev.dto.auth.reqDto.LoginReqDto;
import com.mongsom.dev.dto.auth.reqDto.SignUpReqDto;
import com.mongsom.dev.dto.auth.reqDto.UpdateUserReqDto;
import com.mongsom.dev.dto.auth.respDto.FindIdRespDto;
import com.mongsom.dev.dto.auth.respDto.LoginRespDto;
import com.mongsom.dev.dto.auth.respDto.UserInfoRespDto;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<RespDto<Long>> signUp(@Valid @RequestBody SignUpReqDto reqDto) {
        RespDto<Long> response = authService.signUp(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //아이디 중복확인
    @GetMapping("/duplication-check")
    public ResponseEntity<RespDto<Boolean>> userIdDuplicationCheck(@Valid @RequestParam("userId") String userId) {
        RespDto<Boolean> response = authService.userIdDuplicationCheck(userId);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //로그인
    @PostMapping("/login")
    public ResponseEntity<RespDto<LoginRespDto>> login (@Valid @RequestBody LoginReqDto reqDto) {
        RespDto<LoginRespDto> response = authService.login(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //유저정보조회
    @GetMapping("/{userCode}")
    public ResponseEntity<RespDto<UserInfoRespDto>> getUserInfo(@PathVariable("userCode") Long userCode) {
        RespDto<UserInfoRespDto> response = authService.getUserInfo(userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //회원탈퇴
    @PostMapping("/delete/{userCode}")
    public ResponseEntity<RespDto<String>> deleteUser(@PathVariable("userCode") Long userCode) {
        RespDto<String> response = authService.deleteUser(userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //아이디찾기
    @PostMapping("/find-id")
    public ResponseEntity<RespDto<FindIdRespDto>> findUserId(@Valid @RequestBody FindIdReqDto reqDto) {
        RespDto<FindIdRespDto> response = authService.findUserId(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //비밀번호찾기
    @PostMapping("/find-pw")
    public ResponseEntity<RespDto<Boolean>> findPassword(@Valid @RequestBody FindPwReqDto reqDto) {
        RespDto<Boolean> response = authService.findPassword(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //내정보수정하기
    @PostMapping("/update")
    public ResponseEntity<RespDto<Boolean>> updateUser(@Valid @RequestBody UpdateUserReqDto reqDto) {
        RespDto<Boolean> response = authService.updateUser(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}
