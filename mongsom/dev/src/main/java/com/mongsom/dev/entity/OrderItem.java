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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    
    @Column(name = "user_code", nullable = false)
    private Long userCode;
    
    @Column(name = "received_user_name", nullable = false)
    private String receivedUserName;
    
    @Column(name = "received_user_phone", nullable = false)
    private String receivedUserPhone;
    
    @Column(name = "received_user_zip_code")
    private String receivedUserZipCode;
    
    @Column(name = "received_user_address", nullable = false)
    private String receivedUserAddress;
    
    @Column(name = "received_user_address2", nullable = false)
    private String receivedUserAddress2;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;
    
    @Column(name = "delivery_price", nullable = false)
    private Integer deliveryPrice;
    
    @Column(name = "total_discount_price", nullable = false)
    private Integer totalDiscountPrice;
    
    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;
    
    @Column(name = "delivery_status", nullable = false)
    @Builder.Default
    private String deliveryStatus = "";
    
    @Column(name = "payment_at")
    private LocalDateTime paymentAt;
    
    @Column(name = "delivery_com")
    private String deliveryCom;
    
    @Column(name = "invoice_num")
    private String invoiceNum;
    
    @Column(name = "change_state")
    private Integer changeState;
    
    @Column(name = "approval_change_status")
    @Builder.Default
    private Integer approvalChangeStatus = 0;  // 0=승인대기, 1=승인, 2=반려
    
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
    
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();
}