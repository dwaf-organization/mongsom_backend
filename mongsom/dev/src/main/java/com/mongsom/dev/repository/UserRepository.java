package com.mongsom.dev.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mongsom.dev.entity.User;

import jakarta.validation.constraints.NotBlank;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	//아이디 존재여부 확인
    boolean existsByuserId(String userId);
    
    //userId로 사용자 조회
    Optional<User> findByUserId(@NotBlank String userId);
    
    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);
    
    // 핸드폰 번호로 사용자 조회
    Optional<User> findByPhone(String phone);
    
    // userCode로 사용자 조회
    Optional<User> findUserByUserCode(Long userCode);
    
    // 이름과 이메일로 사용자 조회 (아이디 찾기용)
    Optional<User> findByNameAndEmail(String name, String email);
    
    // 사용자ID, 이름, 휴대전화로 사용자 조회 (비밀번호 찾기용)
    Optional<User> findByUserIdAndNameAndPhone(String userId, String name, String phone);

    // 카카오 로그인 체크 - 이메일과 닉네임으로 사용자 조회
    @Query("SELECT u FROM User u " +
            "WHERE (u.email = :email OR u.userId = :email) " +
            "AND u.name = :nickname " +
            "AND u.provider = 'KAKAO'")
    Optional<User> findByEmailAndNickname(
            @Param("email") String email, 
            @Param("nickname") String nickname);
    
}
