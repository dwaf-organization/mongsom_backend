package com.mongsom.dev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_option")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opt_id")
    private Integer optId;
    
    @Column(name = "opt_name")
    private String optName;
    
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 매핑
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    // 생성자
    public ProductOption(String optName) {
        this.optName = optName;
    }
    
    // productId setter 추가 (Product에서 설정할 수 있도록)
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    // 업데이트 메서드
    public void updateOptionName(String optName) {
        this.optName = optName;
    }
}