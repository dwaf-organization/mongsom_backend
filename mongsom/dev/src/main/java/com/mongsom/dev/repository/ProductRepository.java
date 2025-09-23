package com.mongsom.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // 상품명으로 조회
    Optional<Product> findByName(String name);
    
    // 상품명 중복 체크
    boolean existsByName(String name);
    
    // 프리미엄 상품만 조회 (리스트)
    List<Product> findByPremium(Integer premium);
    
    // 인기순 조회 (주문 횟수 기준, 페이징 지원) - 카운트 쿼리 분리
    @Query(value = "SELECT p.* FROM product p " +
                   "LEFT JOIN (SELECT od.product_id, COUNT(*) as order_count " +
                   "           FROM order_detail od " +
                   "           WHERE od.order_status = 0 " +
                   "           GROUP BY od.product_id) o ON p.product_id = o.product_id " +
                   "ORDER BY COALESCE(o.order_count, 0) DESC, p.product_id DESC", 
           countQuery = "SELECT COUNT(p.product_id) FROM product p",
           nativeQuery = true)
    Page<Product> findAllOrderByPopularityDesc(Pageable pageable);

    
    // 상품명으로 검색 (LIKE 검색)
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    List<Product> findByNameContaining(@Param("keyword") String keyword);
    
    // 생성일 기준 최신 상품 조회
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findAllOrderByCreatedAtDesc();
    
    // 리뷰가 많은 순으로 상품 조회 (네이티브 쿼리 사용)
    @Query(value = "SELECT p.* FROM product p " +
                   "LEFT JOIN (" +
                   "    SELECT product_id, COUNT(*) as review_count " +
                   "    FROM user_review " +
                   "    GROUP BY product_id" +
                   ") r ON p.product_id = r.product_id " +
                   "ORDER BY COALESCE(r.review_count, 0) DESC, p.product_id DESC",
           countQuery = "SELECT COUNT(*) FROM product",
           nativeQuery = true)
    Page<Product> findAllOrderByReviewCountDesc(Pageable pageable);
    
    // 상품과 옵션을 함께 조회 (1단계)
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.productOptions " +
           "WHERE p.productId = :productId")
    Optional<Product> findByIdWithOptions(@Param("productId") Integer productId);
    
    // 상품과 이미지를 함께 조회 (2단계)
    @Query("SELECT DISTINCT p FROM Product p " +
           "LEFT JOIN FETCH p.productImages " +
           "WHERE p.productId = :productId")
    Optional<Product> findByIdWithImages(@Param("productId") Integer productId);
    
    // 기본 상품 조회
    @Query("SELECT p FROM Product p WHERE p.productId = :productId")
    Optional<Product> findByIdWithoutFetch(@Param("productId") Integer productId);
    
    
    //관리자페이지
    
    // 상품명으로 검색 (LIKE %name%)
    Page<Product> findByNameContaining(String name, Pageable pageable);
    
    // 프리미엄 여부로 검색
    Page<Product> findByPremium(Integer premium, Pageable pageable);
    
    // 상품명 + 프리미엄 조건 모두 적용
    Page<Product> findByNameContainingAndPremium(String name, Integer premium, Pageable pageable);
}