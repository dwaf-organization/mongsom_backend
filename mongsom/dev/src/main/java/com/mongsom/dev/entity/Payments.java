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
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payments {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;  // 단일 PK, AUTO_INCREMENT
    
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "user_code", nullable = false)
    private Long userCode;
    
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;  // 신용카드, 계좌이체, 카카오페이, 토스페이 등
    
    @Column(name = "payment_amount")
    private Integer paymentAmount;  // 결제금액
    
    @Column(name = "payment_status")
    @Builder.Default
    private String paymentStatus = "PENDING";  // PENDING, COMPLETED, FAILED, CANCELLED
    
    @Column(name = "payment_key")
    private String paymentKey;  // PG사에서 제공하는 결제 고유키
    
    @Column(name = "pg_provider")
    private String pgProvider;  // PG사 (토스페이먼츠, 카카오페이 등)
    
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
}