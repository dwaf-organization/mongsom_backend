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
@Table(name = "product_img")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImg {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_img_id")
    private Integer productImgId;
    
    @Column(name = "product_img_url", nullable = false)
    private String productImgUrl;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 매핑
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    // 생성자
    public ProductImg(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }
    
    // 업데이트 메서드
    public void updateImageUrl(String productImgUrl) {
        this.productImgUrl = productImgUrl;
    }
}