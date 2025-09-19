package com.mongsom.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    // 사용자별 장바구니 조회
    List<Cart> findByUserCodeOrderByCreatedAtDesc(Integer userCode);
    
    // 사용자의 장바구니 조회 (최신순)
    @Query("SELECT c FROM Cart c " +
           "JOIN FETCH c.product p " +
           "LEFT JOIN FETCH c.productOption po " +
           "WHERE c.userCode = :userCode " +
           "ORDER BY c.createdAt DESC")
    List<Cart> findByUserCodeWithProductAndOption(@Param("userCode") Integer userCode);
    
    // 사용자의 체크된 장바구니 아이템 조회
    @Query("SELECT c FROM Cart c " +
           "JOIN FETCH c.product p " +
           "LEFT JOIN FETCH c.productOption po " +
           "WHERE c.userCode = :userCode AND c.checkStatus = 1 " +
           "ORDER BY c.createdAt DESC")
    List<Cart> findByUserCodeAndCheckStatusTrueWithProductAndOption(@Param("userCode") Integer userCode);
    
    // 사용자의 장바구니 개수 조회
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.userCode = :userCode")
    Long countByUserCode(@Param("userCode") Integer userCode);
    
    // 사용자의 특정 상품+옵션 조합 존재 여부 확인
    @Query("SELECT c FROM Cart c WHERE c.userCode = :userCode AND c.productId = :productId AND c.optId = :optId")
    List<Cart> findByUserCodeAndProductIdAndOptId(
            @Param("userCode") Integer userCode, 
            @Param("productId") Integer productId, 
            @Param("optId") Integer optId);
    
    // 사용자, 상품, 옵션이 모두 일치하는 항목만 삭제
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userCode = :userCode AND c.productId = :productId AND c.optId = :optId")
    int deleteByUserCodeAndProductIdAndOptId(@Param("userCode") Integer userCode, 
                                            @Param("productId") Integer productId, 
                                            @Param("optId") Integer optId);
    
    // 옵션이 없는 상품의 경우
    @Query("SELECT c FROM Cart c WHERE c.userCode = :userCode AND c.productId = :productId AND c.optId IS NULL")
    List<Cart> findByUserCodeAndProductIdAndOptIdIsNull(
            @Param("userCode") Integer userCode, 
            @Param("productId") Integer productId);
    
    // 사용자의 상품 모든 옵션 조회
    @Query("SELECT c FROM Cart c WHERE c.userCode = :userCode AND c.productId = :productId")
    List<Cart> findByUserCodeAndProductId(
            @Param("userCode") Integer userCode, 
            @Param("productId") Integer productId);
    
    // 사용자의 상품 삭제
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userCode = :userCode AND c.productId = :productId")
    int deleteByUserCodeAndProductId(
            @Param("userCode") Integer userCode, 
            @Param("productId") Integer productId);
    
    // 사용자의 특정 장바구니 아이템 조회
    @Query("SELECT c FROM Cart c WHERE c.cartId = :cartId AND c.userCode = :userCode")
    Optional<Cart> findByCartIdAndUserCode(
            @Param("cartId") Integer cartId, 
            @Param("userCode") Integer userCode);
    
    // 사용자별 전체 체크 상태 변경
    @Modifying
    @Query("UPDATE Cart c SET c.checkStatus = :checkStatus WHERE c.userCode = :userCode")
    int updateAllCheckStatusByUserCode(@Param("userCode") Integer userCode, 
                                      @Param("checkStatus") Integer checkStatus);
    
    // 회원탈퇴용 - userCode로 모든 장바구니 데이터 삭제
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.userCode = :userCode")
    int deleteByUserCode(@Param("userCode") Integer userCode);
}