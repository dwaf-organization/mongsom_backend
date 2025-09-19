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

            // 1단계: Product 엔티티 생성 (옵션/이미지 제외)
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

            // 2단계: Product 먼저 저장해서 productId 생성
            Product savedProduct = productRepository.save(product);
            log.info("Product 저장 완료 - productId: {}", savedProduct.getProductId());

            // 3단계: 이제 productId가 있으니 옵션 추가
            for (String optName : reqDto.getOptNames()) {
                if (optName != null && !optName.trim().isEmpty()) {
                    ProductOption productOption = new ProductOption(optName.trim());
                    savedProduct.addProductOption(productOption);
                }
            }

            // 4단계: 상품 이미지 추가
            for (String imgUrl : reqDto.getProductImgUrls()) {
                if (imgUrl != null && !imgUrl.trim().isEmpty()) {
                    ProductImg productImg = new ProductImg(imgUrl.trim());
                    savedProduct.addProductImage(productImg);
                }
            }

            // 유효성 검증
            if (savedProduct.getProductOptions().isEmpty()) {
                log.warn("상품 옵션이 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-3)
                        .data(false)
                        .build();
            }

            if (savedProduct.getProductImages().isEmpty()) {
                log.warn("상품 이미지가 없음 - name: {}", reqDto.getName());
                return RespDto.<Boolean>builder()
                        .code(-4)
                        .data(false)
                        .build();
            }

            // 5단계: 옵션과 이미지까지 포함해서 다시 저장
            Product finalProduct = productRepository.save(savedProduct);

            log.info("상품 등록 완료 - productId: {}, name: {}, optionCount: {}, imageCount: {}",
                    finalProduct.getProductId(), finalProduct.getName(),
                    finalProduct.getProductOptions().size(), finalProduct.getProductImages().size());

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