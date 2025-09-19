package com.mongsom.dev.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_review")
@Getter
@Setter
@NoArgsConstructor
public class UserReview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    
    @Column(name = "user_code", nullable = false)
    private Integer userCode;
    
    @Column(name = "order_detail_id", nullable = false)
    private Integer orderDetailId;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    // ⭐ 추가된 컬럼
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "review_rating")
    private Integer reviewRating;
    
    @Column(name = "review_content", length = 255)
    private String reviewContent;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // Builder 패턴을 위한 생성자
    @Builder
    public UserReview(Integer userCode, Integer orderDetailId, Integer productId, Integer orderId,
                     Integer reviewRating, String reviewContent, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userCode = userCode;
        this.orderDetailId = orderDetailId;
        this.productId = productId;
        this.orderId = orderId;
        this.reviewRating = reviewRating;
        this.reviewContent = reviewContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}