package com.mongsom.dev.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.ProductRegistReqDto;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.entity.ProductImg;
import com.mongsom.dev.entity.ProductOption;
import com.mongsom.dev.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {
    
    private final ProductRepository productRepository;
    
    @Transactional
    public RespDto<Boolean> registProduct(ProductRegistReqDto reqDto) {
        try {
            log.info("상품 등록 시작 - name: {}", reqDto.getName());
            
            // 상품명 중복 체크
            if (productRepository.existsByName(reqDto.getName())) {
                log.warn("이미 존재하는 상품명 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // Product 엔티티 생성
            Product product = Product.builder()
                    .name(reqDto.getName())
                    .contents(reqDto.getContents())
                    .premium(reqDto.getPremium())
                    .price(reqDto.getPrice())
                    .salesMargin(reqDto.getSalesMargin())
                    .discountPer(reqDto.getDiscountPer())
                    .discountPrice(reqDto.getDiscountPrice())
                    .deliveryPrice(reqDto.getDeliveryPrice())
                    .build();
            
            // 상품 옵션 추가
            for (String optName : reqDto.getOptNames()) {
                if (optName != null && !optName.trim().isEmpty()) {
                    ProductOption productOption = new ProductOption(optName.trim());
                    product.addProductOption(productOption);
                }
            }
            
            // 상품 이미지 추가
            for (String imgUrl : reqDto.getProductImgUrls()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImg productImg = new ProductImg(imgUrl.trim());
                    product.addProductImage(productImg);
                }
            }
            
            // 유효성 검증
            if (product.getProductOptions().isEmpty()) {
                log.warn("상품 옵션이 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-3)
                        .data(false)
                        .build();
            }
            
            if (product.getProductImages().isEmpty()) {
                log.warn("상품 이미지가 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-4)
                        .data(false)
                        .build();
            }
            
            // 상품 저장 (CASCADE로 인해 옵션과 이미지도 함께 저장됨)
            Product savedProduct = productRepository.save(product);
            
            log.info("상품 등록 완료 - productId: {}, name: {}, optionCount: {}, imageCount: {}", 
                    savedProduct.getProductId(), savedProduct.getName(), 
                    savedProduct.getProductOptions().size(), savedProduct.getProductImages().size());
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("상품 등록 실패 - name: {}", reqDto.getName(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
}