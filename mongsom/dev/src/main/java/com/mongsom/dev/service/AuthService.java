package com.mongsom.dev.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.auth.reqDto.FindIdReqDto;
import com.mongsom.dev.dto.auth.reqDto.FindPwReqDto;
import com.mongsom.dev.dto.auth.reqDto.LoginReqDto;
import com.mongsom.dev.dto.auth.reqDto.SignUpReqDto;
import com.mongsom.dev.dto.auth.reqDto.UpdateUserReqDto;
import com.mongsom.dev.dto.auth.respDto.FindIdRespDto;
import com.mongsom.dev.dto.auth.respDto.LoginRespDto;
import com.mongsom.dev.dto.auth.respDto.UserInfoRespDto;
import com.mongsom.dev.entity.Provider;
import com.mongsom.dev.entity.Role;
import com.mongsom.dev.entity.Status;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.CartRepository;
import com.mongsom.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JavaMailSender mailSender;
    
    // 회원가입
    @Transactional
    public RespDto<Long> signUp(SignUpReqDto reqDto) {
        try {
            String encodePassword = passwordEncoder.encode(reqDto.getPassword());
            
            User user = User.builder()
                    .userId(reqDto.getUserId())
                    .email(reqDto.getEmail())
                    .password(encodePassword)
                    .name(reqDto.getName())
                    .phone(reqDto.getPhone())
                    .zipCode(reqDto.getZipCode())
                    .address(reqDto.getAddress())
                    .address2(reqDto.getAddress2())
                    .birth(reqDto.getBirth())
                    .agreeMain(reqDto.isAgreeMain())
                    .agreeShopping(reqDto.isAgreeShopping())
                    .agreeSms(reqDto.isAgreeSms())
                    .agreeEmail(reqDto.isAgreeEmail())
                    .role(Role.USER)
                    .provider(reqDto.getProvider())
                    .status(Status.ACTIVE)
                    .build();
            
            User savedUser = userRepository.save(user);

            return RespDto.<Long>builder()
                    .code(1)
                    .data(savedUser.getUserCode())
                    .build();

        } catch (Exception e) {
            log.error("회원 가입 실패", e);
            return RespDto.<Long>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }

    //회원가입시 아이디중복확인
    public RespDto<Boolean> userIdDuplicationCheck(String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                return RespDto.fail("유효하지 않은 아이디입니다.");
            }
            if (userRepository.existsByuserId(userId)) {
                return RespDto.success("이미 사용 중인 이메일입니다.", false);
            }

            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();

        } catch (Exception e) {
            log.error("이메일 중복확인 실패", e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //로그인
    public RespDto<LoginRespDto> login(LoginReqDto reqDto) {
        LoginRespDto data;
        try {
            Optional<User> optionalUser = userRepository.findByUserId(reqDto.getUserId());

            if (optionalUser.isEmpty()) {
                return RespDto.fail("존재하지 않는 아이디입니다.");
            }

            User user = optionalUser.get();
            
            // 1. 상태 확인
            if (user.getStatus() != Status.ACTIVE) {
                return RespDto.fail("비활성화된 계정입니다. 관리자에게 문의하세요.");
            }
            

            if (!passwordEncoder.matches(reqDto.getPassword(), user.getPassword())) {
                return RespDto.fail("비밀번호가 일치하지 않습니다.");
            }

            return RespDto.<LoginRespDto>builder()
                    .code(1)
                    .data(new LoginRespDto(user))
                    .build();

        } catch (Exception e) {
            log.error("로그인 실패", e);
            return RespDto.<LoginRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //유저정보 가져오기
    public RespDto<UserInfoRespDto> getUserInfo(Long userCode) {
        try {
            Optional<User> userOptional = userRepository.findUserByUserCode(userCode);
            
            if (userOptional.isEmpty()) {
                return RespDto.<UserInfoRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            User user = userOptional.get();
            UserInfoRespDto useInfoRespDto = UserInfoRespDto.from(user);

            return RespDto.<UserInfoRespDto>builder()
                    .code(1)
                    .data(useInfoRespDto)
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<UserInfoRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //회원탈퇴
    @Transactional
    public RespDto<String> deleteUser(Long userCode) {
        try {
            Optional<User> userOptional = userRepository.findUserByUserCode(userCode);
            
            if (userOptional.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("사용자를 찾을 수 없습니다.")
                        .build();
            }
            
            User user = userOptional.get();
            
            // 이미 탈퇴한 사용자인지 확인
            if (user.getStatus() == Status.INACTIVE) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("이미 탈퇴한 사용자입니다.")
                        .build();
            }
            
            // cart 테이블에서 해당 userCode 데이터 모두 삭제
            int cartDeleteCount = cartRepository.deleteByUserCode(userCode);
            
            // 탈퇴 처리 (status를 INACTIVE로 변경)
            user.updateStatus(Status.INACTIVE);
            user.updateUserIdForWithdrawal(userCode + "_deleted");
            user.updateEmailForWithdrawal(userCode + "_deleted@deleted.com");
            userRepository.save(user);

            return RespDto.<String>builder()
                    .code(1)
                    .data("회원 탈퇴가 완료되었습니다.")
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("회원 탈퇴 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }
    
    //아이디찾기
    @Transactional
    public RespDto<FindIdRespDto> findUserId(FindIdReqDto reqDto) {
        try {            
            Optional<User> userOptional = userRepository.findByNameAndEmail(reqDto.getName(), reqDto.getEmail());
            
            if (userOptional.isEmpty()) {
                return RespDto.<FindIdRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            User user = userOptional.get();
            
            // 탈퇴한 사용자인지 확인
            if (user.getStatus() == Status.INACTIVE) {
                return RespDto.<FindIdRespDto>builder()
                        .code(-2)
                        .data(null)
                        .build();
            }
            
            FindIdRespDto responseDto = FindIdRespDto.from(user.getUserId());
            
            return RespDto.<FindIdRespDto>builder()
                    .code(1)
                    .data(responseDto)
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<FindIdRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    //비밀번호찾기
    @Transactional
    public RespDto<String> findPassword(FindPwReqDto reqDto) {
        try {
            Optional<User> userOptional = userRepository.findByUserIdAndNameAndPhone(
                    reqDto.getUserId(), reqDto.getName(), reqDto.getPhone());
            
            if (userOptional.isEmpty()) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("사용자가 일치하지않습니다.")
                        .build();
            }
            
            User user = userOptional.get();
            
            // 탈퇴한 사용자인지 확인
            if (user.getStatus() == Status.INACTIVE) {
                return RespDto.<String>builder()
                        .code(-1)
                        .data("이미 탈퇴한 사용자입니다.")
                        .build();
            }
            
            // 임시 비밀번호 생성
            String tempPassword = generateTempPassword();
            
            // 사용자 비밀번호 업데이트
            String encodedPassword = passwordEncoder.encode(tempPassword);
            user.updatePassword(encodedPassword);
            userRepository.save(user);
            
            // 이메일 발송
            sendPasswordEmail(reqDto.getEmail(), tempPassword);

            return RespDto.<String>builder()
                    .code(1)
                    .data("이메일 발송에 성공했습니다.")
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<String>builder()
                    .code(-1)
                    .data("요청처리를 실패했습니다.")
                    .build();
        }
    }
    /**
     * 임시 비밀번호 생성 (8자리)
     */
    private String generateTempPassword() {
        String chars = "abcdefghijklmnopqrstuvwxyz123456789!@#";
        StringBuilder tempPassword = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < 8; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return tempPassword.toString();
    }
    
    /**
     * 임시 비밀번호 이메일 발송
     */
    private void sendPasswordEmail(String email, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("몽솜 임시 비밀번호 안내");
            message.setText("안녕하세요. 몽솜입니다.\n\n" +
                           "비밀번호는\n" +
                           tempPassword + "\n" +
                           "입니다.\n\n" +
                           "로그인 후 반드시 비밀번호를 변경해주세요.");
            
            mailSender.send(message);
            log.info("임시 비밀번호 이메일 발송 완료 - email: {}", email);
            
        } catch (Exception e) {
            log.error("이메일 발송 실패 - email: {}, error: {}", email, e.getMessage());
            throw new RuntimeException("이메일 발송에 실패했습니다.");
        }
    }
    //회원정보수정
    @Transactional
    public RespDto<Boolean> updateUser(UpdateUserReqDto reqDto) {
        try {
        	// 사용자존재여부확인
            Optional<User> userOptional = userRepository.findUserByUserCode(reqDto.getUserCode());
            
            if (userOptional.isEmpty()) {
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            User user = userOptional.get();
            
            // 탈퇴한 사용자인지 확인
            if (user.getStatus() == Status.INACTIVE) {
                return RespDto.<Boolean>builder()
                        .code(-4)
                        .data(false)
                        .build();
            }
            
            // 이메일 중복 체크 (본인 제외)
//            Optional<User> existingUserWithEmail = userRepository.findByEmail(reqDto.getEmail());
//            if (existingUserWithEmail.isPresent() && 
//                !existingUserWithEmail.get().getUserCode().equals(reqDto.getUserCode())) {
//                return RespDto.<Boolean>builder()
//                        .code(-2)
//                        .data(false)
//                        .build();
//            }
            
            // 핸드폰 번호 중복 체크 (본인 제외)
//            Optional<User> existingUserWithPhone = userRepository.findByPhone(reqDto.getPhone());
//            if (existingUserWithPhone.isPresent() && 
//                !existingUserWithPhone.get().getUserCode().equals(reqDto.getUserCode())) {
//                return RespDto.<Boolean>builder()
//                        .code(-3)
//                        .data(false)
//                        .build();
//            }
            
            // 비밀번호 업데이트 (password가 있는 경우만)
            if (reqDto.getPassword() != null && !reqDto.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(reqDto.getPassword());
                user.updatePassword(encodedPassword);
                log.info("비밀번호 업데이트 완료 - userCode: {}", reqDto.getUserCode());
            } else {
                log.info("비밀번호 업데이트 생략 - userCode: {}", reqDto.getUserCode());
            }
            
            // 기본 프로필 정보 업데이트
            user.updateProfile(
                reqDto.getName(),
                reqDto.getPhone(),
                reqDto.getEmail(),
                reqDto.getZipCode(),
                reqDto.getAddress(),
                reqDto.getAddress2(),
                reqDto.getBirth()
            );
            
            userRepository.save(user);
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
}
