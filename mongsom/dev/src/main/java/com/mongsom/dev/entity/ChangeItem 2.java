package com.mongsom.dev.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "change_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "change_id")
    private Integer changeId;
    
    @Column(name = "order_item_id", nullable = false)
    private Integer orderItemId;
    
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "user_code", nullable = false)
    private Integer userCode;
    
    @Column(name = "change_status", columnDefinition = "int(11) DEFAULT 0")
    private Integer changeStatus = 0; // 0=주문, 1=교환, 2=반품
    
    @Column(name = "contents", length = 255)
    private String contents;
    
    @Column(name = "approval_status", columnDefinition = "int(11) DEFAULT 0")
    private Integer approvalStatus = 0; // 0=대기, 1=승인, 2=반려
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 상태 상수 정의
    public static class ChangeStatus {
        public static final int ORDER = 0;      // 주문
        public static final int EXCHANGE = 1;   // 교환
        public static final int RETURN = 2;     // 반품
    }
    
    public static class ApprovalStatus {
        public static final int PENDING = 0;    // 대기
        public static final int APPROVED = 1;   // 승인
        public static final int REJECTED = 2;   // 반려
    }
}