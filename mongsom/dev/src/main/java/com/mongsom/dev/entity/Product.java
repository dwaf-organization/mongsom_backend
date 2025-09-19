package com.mongsom.dev.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents;
    
    @Column(name = "premium")
    private Integer premium; // 0=일반상품, 1=프리미엄상품
    
    @Column(name = "price", nullable = false)
    private Integer price;
    
    @Column(name = "sales_margin")
    private Integer salesMargin;
    
    @Column(name = "discount_per")
    private Integer discountPer;
    
    @Column(name = "discount_price")
    private Integer discountPrice;
    
    @Column(name = "delivery_price")
    private Integer deliveryPrice;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 연관관계 매핑
    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList<>();
    
    @Builder.Default
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImg> productImages = new ArrayList<>();
    
    // 비즈니스 메서드
    public void addProductOption(ProductOption productOption) {
        if (this.productOptions == null) {
            this.productOptions = new ArrayList<>();
        }
        this.productOptions.add(productOption);
        
        // productId를 직접 설정해줘야 함
        productOption.setProductId(this.productId);
        productOption.setProduct(this);
    }
    
    public void addProductImage(ProductImg productImg) {
        if (this.productImages == null) {
            this.productImages = new ArrayList<>();
        }
        this.productImages.add(productImg);
        
        // ProductImg는 관계 매핑만 있으므로 이대로 OK
        productImg.setProduct(this);
    }
    
    // 업데이트 메서드들
    public void updateProduct(String name, String contents, Integer premium, Integer price, 
                             Integer salesMargin, Integer discountPer, Integer discountPrice, Integer deliveryPrice) {
        this.name = name;
        this.contents = contents;
        this.premium = premium;
        this.price = price;
        this.salesMargin = salesMargin;
        this.discountPer = discountPer;
        this.discountPrice = discountPrice;
        this.deliveryPrice = deliveryPrice;
    }
}