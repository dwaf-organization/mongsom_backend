package com.mongsom.dev.service.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.user.reqDto.AdminLoginReqDto;
import com.mongsom.dev.dto.admin.user.respDto.AdminUserListRespDto;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 관리자 회원 정보 조회 (페이지네이션)
     * @param page 페이지 번호 (1부터 시작)
     * @param size 페이지 크기
     * @return 회원 목록 및 페이지 정보
     */
    public RespDto<AdminUserListRespDto> getUserList(Integer page, Integer size) {
        try {
            log.info("=== 관리자 회원 정보 조회 시작 - page: {}, size: {} ===", page, size);
            
            // 페이지 번호는 1부터 시작하지만 Spring Data JPA는 0부터 시작
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userCode").descending());
            
            // 회원 정보 조회
            Page<User> userPage = userRepository.findAll(pageable);
            
            // User 엔티티를 UserInfo DTO로 변환
            List<AdminUserListRespDto.UserInfo> userInfoList = userPage.getContent().stream()
                    .map(user -> AdminUserListRespDto.UserInfo.builder()
                            .userCode(user.getUserCode())
                            .userId(user.getUserId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .build())
                    .collect(Collectors.toList());
            
            // 페이지네이션 정보 생성
            AdminUserListRespDto.Pagination pagination = AdminUserListRespDto.Pagination.builder()
                    .currentPage(page)
                    .totalPage(userPage.getTotalPages())
                    .size(size)
                    .hasNext(userPage.hasNext())
                    .build();
            
            // 최종 응답 DTO 생성
            AdminUserListRespDto responseDto = AdminUserListRespDto.builder()
                    .users(userInfoList)
                    .pagination(pagination)
                    .build();
            
            log.info("=== 회원 정보 조회 완료 - 조회된 회원 수: {}, 전체 페이지: {}, 다음 페이지 존재: {} ===", 
                    userInfoList.size(), userPage.getTotalPages(), userPage.hasNext());
            
            return RespDto.<AdminUserListRespDto>builder()
                    .code(1)
                    .data(responseDto)
                    .build();
            
        } catch (Exception e) {
            log.error("회원 정보 조회 실패 - page: {}, size: {}, error: {}", page, size, e.getMessage(), e);
            
            return RespDto.<AdminUserListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    /**
     * 관리자 로그인
     * @param reqDto 아이디, 비밀번호
     * @return 로그인 성공 시 관리자 정보, 실패 시 null
     */
    public RespDto<Long> adminLogin(AdminLoginReqDto reqDto) {
        try {
            log.info("=== 관리자 로그인 시도 - userId: {} ===", reqDto.getUserId());
            
            // 1. userId와 provider='ADMIN'으로 사용자 조회
            Optional<User> adminOpt = userRepository.findAdminByUserId(reqDto.getUserId());
            
            if (adminOpt.isEmpty()) {
                log.warn("관리자를 찾을 수 없음 - userId: {}", reqDto.getUserId());
                return RespDto.<Long>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            User admin = adminOpt.get();
            
            // 2. 비밀번호 확인
            if (!passwordEncoder.matches(reqDto.getPassword(), admin.getPassword())) {
                log.warn("비밀번호 불일치 - userId: {}", reqDto.getUserId());
                return RespDto.<Long>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            // 3. 로그인 성공 - 관리자 정보 반환
            log.info("=== 관리자 로그인 성공 - userCode: {}, userId: {} ===", 
                    admin.getUserCode(), admin.getUserId());
            
            return RespDto.<Long>builder()
                    .code(1)
                    .data(admin.getUserCode())
                    .build();
            
        } catch (Exception e) {
            log.error("관리자 로그인 실패 - userId: {}, error: {}", 
                    reqDto.getUserId(), e.getMessage(), e);
            
            return RespDto.<Long>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
}