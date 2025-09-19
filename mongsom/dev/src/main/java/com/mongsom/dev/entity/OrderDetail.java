package com.mongsom.dev.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;
    
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "user_code", nullable = false)
    private Integer userCode;
    
    @Column(name = "opt_id", nullable = false)
    private Integer optId;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @Column(name = "quantity")
    private Integer quantity;
    
    @Column(name = "price")
    private Integer price;
    
    @Column(name = "review_status")
    private Integer reviewStatus;
    
    @Column(name = "order_status")
    @Builder.Default
    private Integer orderStatus = 0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderItem orderItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_code", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // ProductOption 연관관계는 복합키 참조로 복잡하므로 주석 처리
    // 필요시 optId 필드로 직접 조회하여 사용
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumns({
    //     @JoinColumn(name = "product_id", insertable = false, updatable = false),
    //     @JoinColumn(name = "opt_id", insertable = false, updatable = false)
    // })
    // private ProductOption productOption;
}