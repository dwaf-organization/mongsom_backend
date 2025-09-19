package com.mongsom.dev.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.UserReview;

@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {
    
    // 상품의 리뷰 조회 (최신순, 페이징)
    @Query("SELECT ur FROM UserReview ur " +
           "JOIN FETCH ur.user u " +
           "WHERE ur.productId = :productId " +
           "ORDER BY ur.createdAt DESC")
    Page<UserReview> findByProductIdOrderByCreatedAtDesc(@Param("productId") Integer productId, Pageable pageable);
    
    // 상품의 리뷰 개수 조회
    @Query("SELECT COUNT(ur) FROM UserReview ur WHERE ur.productId = :productId")
    Long countByProductId(@Param("productId") Integer productId);
    
    // 사용자의 리뷰 조회
    @Query("SELECT ur FROM UserReview ur " +
           "JOIN FETCH ur.user u " +
           "WHERE ur.userCode = :userCode " +
           "ORDER BY ur.createdAt DESC")
    Page<UserReview> findByUserCodeOrderByCreatedAtDesc(@Param("userCode") Integer userCode, Pageable pageable);
    
    // 상품별 평균 평점 조회
    @Query("SELECT AVG(ur.reviewRating) FROM UserReview ur WHERE ur.productId = :productId")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);
}