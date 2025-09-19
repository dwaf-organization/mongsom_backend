package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.ReviewImg;

@Repository
public interface ReviewImgRepository extends JpaRepository<ReviewImg, Integer> {
    
    // 리뷰의 이미지들 조회
    @Query("SELECT ri FROM ReviewImg ri WHERE ri.reviewId = :reviewId ORDER BY ri.createdAt")
    List<ReviewImg> findByReviewIdOrderByCreatedAt(@Param("reviewId") Integer reviewId);
    
    // 여러 리뷰의 이미지들 조회
    @Query("SELECT ri FROM ReviewImg ri WHERE ri.reviewId IN :reviewIds ORDER BY ri.reviewId, ri.createdAt")
    List<ReviewImg> findByReviewIdInOrderByReviewIdAndCreatedAt(@Param("reviewIds") List<Integer> reviewIds);
    
    // 리뷰 ID로 리뷰 이미지 URL 조회
    @Query(value = "SELECT review_img_url FROM review_img WHERE review_id = :reviewId ORDER BY review_img_id", nativeQuery = true)
    List<Object[]> findByReviewId(@Param("reviewId") Integer reviewId);
    
    // 리뷰 ID로 리뷰 이미지 엔티티 조회
    List<ReviewImg> findByReviewIdOrderByReviewImgId(Integer reviewId);
    
    // 리뷰 ID로 기존 이미지 삭제 (수정 시 사용)
    @Modifying
    @Query("DELETE FROM ReviewImg ri WHERE ri.reviewId = :reviewId")
    void deleteByReviewId(@Param("reviewId") Integer reviewId);
}