package com.mongsom.dev.repository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

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
    Optional<User> findUserByUserCode(Integer userCode);
    
    // 이름과 이메일로 사용자 조회 (아이디 찾기용)
    Optional<User> findByNameAndEmail(String name, String email);
    
    // 사용자ID, 이름, 이메일로 사용자 조회 (비밀번호 찾기용)
    Optional<User> findByUserIdAndNameAndEmail(String userId, String name, String email);

}
