package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.ProductImg;

@Repository
public interface ProductImgRepository extends JpaRepository<ProductImg, Integer> {

    // 상품의 이미지들 조회
    List<ProductImg> findByProductProductId(Integer productId);
    
    // 상품의 이미지 개수 조회
    int countByProductProductId(Integer productId);
    
    // 이미지 URL로 조회
    List<ProductImg> findByProductImgUrl(String productImgUrl);
    
    // 상품의 특정 이미지 URL 존재 여부 확인
    boolean existsByProductProductIdAndProductImgUrl(Integer productId, String productImgUrl);
    
    // 상품의 이미지들 조회 (생성일순 정렬)
    @Query("SELECT pi FROM ProductImg pi WHERE pi.product.productId = :productId ORDER BY pi.createdAt")
    List<ProductImg> findByProductIdOrderByCreatedAt(@Param("productId") Integer productId);
    
    // 여러 상품의 이미지들 조회
    @Query("SELECT pi FROM ProductImg pi WHERE pi.product.productId IN :productIds ORDER BY pi.product.productId, pi.createdAt")
    List<ProductImg> findByProductIdInOrderByProductIdAndCreatedAt(@Param("productIds") List<Integer> productIds);
    
    // 상품별 이미지 URL 리스트 조회 (올바른 컬럼명 사용)
    @Query(value = "SELECT product_img_url FROM product_img WHERE product_id = :productId ORDER BY product_img_id", nativeQuery = true)
    List<String> findImgUrlsByProductId(@Param("productId") Integer productId);
    
    // 상품 수정용 - productId로 모든 이미지 삭제
    @Modifying
    @Query("DELETE FROM ProductImg p WHERE p.product.productId = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}