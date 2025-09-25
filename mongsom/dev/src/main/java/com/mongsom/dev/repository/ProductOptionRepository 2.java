package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.ProductOption;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {
    
    // 상품의 옵션들 조회
    List<ProductOption> findByProductProductId(Integer productId);
    
    // 상품의 옵션 개수 조회
    int countByProductProductId(Integer productId);
    
    // 상품의 옵션명 존재 여부 확인
    boolean existsByProductProductIdAndOptName(Integer productId, String optName);
    
    // opt_id로 직접 옵션명 조회 (더 간단한 방법)
    @Query(value = "SELECT opt_name FROM product_option WHERE opt_id = :optId", nativeQuery = true)
    String findOptNameByOptId(@Param("optId") Integer optId);
    
    // 기존 방식도 유지 (productId + optId 조합)
    @Query(value = "SELECT opt_name FROM product_option WHERE product_id = :productId AND opt_id = :optId", nativeQuery = true)
    String findOptNameByProductIdAndOptId(@Param("productId") Integer productId, @Param("optId") Integer optId);
    
    // 디버깅용: 특정 opt_id 존재 확인
    @Query(value = "SELECT COUNT(*) FROM product_option WHERE opt_id = :optId", nativeQuery = true)
    Integer countByOptId(@Param("optId") Integer optId);
}