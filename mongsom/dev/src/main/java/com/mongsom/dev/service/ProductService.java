package com.mongsom.dev.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.product.respDto.ProductDetailRespDto;
import com.mongsom.dev.dto.product.respDto.ProductListRespDto;
import com.mongsom.dev.dto.product.respDto.ProductReviewRespDto;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.entity.ProductImg;
import com.mongsom.dev.entity.ReviewImg;
import com.mongsom.dev.entity.UserReview;
import com.mongsom.dev.repository.ProductImgRepository;
import com.mongsom.dev.repository.ProductRepository;
import com.mongsom.dev.repository.ReviewImgRepository;
import com.mongsom.dev.repository.UserReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserReviewRepository userReviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ProductImgRepository productImgRepository;
    
    //전체상품조회
    @Transactional(readOnly = true)
    public RespDto<ProductListRespDto> getAllProducts(Pageable pageable) {
        try {
            Page<Product> productPage = productRepository.findAll(pageable);
            
            // 상품 이미지 조회 및 매핑
            Map<Integer, List<String>> productImgMap = getProductImagesMap(productPage.getContent());
            
            ProductListRespDto productListResDto = ProductListRespDto.from(productPage, productImgMap);
            
            //전체 상품 조회 성공
            return RespDto.<ProductListRespDto>builder()
                    .code(1)
                    .data(productListResDto)
                    .build();
                    
        } catch (Exception e) {
            //전체 상품 조회 실
            return RespDto.<ProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //프리미엄상품조회
    @Transactional(readOnly = true)
    public RespDto<ProductListRespDto> getPremiumProducts(Pageable pageable) {
        try {
            Page<Product> productPage = productRepository.findByPremium(1, pageable);
            
            // 상품 이미지 조회 및 매핑
            Map<Integer, List<String>> productImgMap = getProductImagesMap(productPage.getContent());
            
            ProductListRespDto productListResDto = ProductListRespDto.from(productPage, productImgMap);
            
            //프리미엄 상품 조회 성공
            return RespDto.<ProductListRespDto>builder()
                    .code(1)
                    .data(productListResDto)
                    .build();
                    
        } catch (Exception e) {
        	//프리미엄 상품 조회 성공
            return RespDto.<ProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //인기상품조회
    @Transactional(readOnly = true)
    public RespDto<ProductListRespDto> getPopularProducts(Pageable pageable) {
        try {
            log.info("인기 상품 조회 시작 - page: {}, size: {}", 
                    pageable.getPageNumber() + 1, pageable.getPageSize());
            
            Page<Product> productPage = productRepository.findAllOrderByPopularityDesc(pageable);
            
            if (productPage.isEmpty()) {
                log.info("조회된 인기 상품이 없음");
                return RespDto.<ProductListRespDto>builder()
                        .code(1)
                        .data(ProductListRespDto.builder()
                                .items(List.of())
                                .pagination(ProductListRespDto.PaginationDto.from(productPage))
                                .build())
                        .build();
            }
            
            // 상품 이미지 조회 및 매핑
            Map<Integer, List<String>> productImgMap = getProductImagesMap(productPage.getContent());
            
            ProductListRespDto productListResDto = ProductListRespDto.from(productPage, productImgMap);
            
            log.info("인기 상품 조회 완료 - totalElements: {}, totalPages: {}, currentPage: {}", 
                    productPage.getTotalElements(), productPage.getTotalPages(), 
                    productPage.getNumber() + 1);
            
            return RespDto.<ProductListRespDto>builder()
                    .code(1)
                    .data(productListResDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("인기 상품 조회 실패 - page: {}, size: {}", 
                    pageable.getPageNumber() + 1, pageable.getPageSize(), e);
            return RespDto.<ProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    //최신상품조회
    @Transactional(readOnly = true)
    public RespDto<ProductListRespDto> getNewProducts(Pageable pageable) {
        try {
            Page<Product> productPage = productRepository.findAll(pageable);
            
            // 상품 이미지 조회 및 매핑
            Map<Integer, List<String>> productImgMap = getProductImagesMap(productPage.getContent());
            
            ProductListRespDto productListResDto = ProductListRespDto.from(productPage, productImgMap);
            
            //최신상품 조회 성공
            return RespDto.<ProductListRespDto>builder()
                    .code(1)
                    .data(productListResDto)
                    .build();
                    
        } catch (Exception e) {
        	//최신상품 조회실패
            return RespDto.<ProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //상품리뷰순조회
    @Transactional(readOnly = true)
    public RespDto<ProductListRespDto> getProductsByReviewCount(Pageable pageable) {
        try {
            Page<Product> productPage = productRepository.findAllOrderByReviewCountDesc(pageable);
            
            // 상품 이미지 조회 및 매핑
            Map<Integer, List<String>> productImgMap = getProductImagesMap(productPage.getContent());
            
            ProductListRespDto productListResDto = ProductListRespDto.from(productPage, productImgMap);
            
            //상품 리뷰순 조회 성공
            return RespDto.<ProductListRespDto>builder()
                    .code(1)
                    .data(productListResDto)
                    .build();
                    
        } catch (Exception e) {
        	//상품 리뷰순 조회 실패
            return RespDto.<ProductListRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    //상품상세조회
    @Transactional(readOnly = true)
    public RespDto<ProductDetailRespDto> getProductDetail(Integer productCode) {
        try {
            // 1단계: 기본 상품 정보 조회
            Optional<Product> productOptional = productRepository.findById(productCode);
            
            if (productOptional.isEmpty()) {
                return RespDto.<ProductDetailRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            Product product = productOptional.get();
            
            // 2단계: 연관된 이미지들과 옵션들이 Lazy Loading으로 자동 조회됨
            // Entity에서 @OneToMany로 설정되어 있으므로 자동으로 product_id로 조회
            
            ProductDetailRespDto productDetailRespDto = ProductDetailRespDto.from(product);
            
            // 상품 상세 조회 성공
            return RespDto.<ProductDetailRespDto>builder()
                    .code(1)
                    .data(productDetailRespDto)
                    .build();
                    
        } catch (Exception e) {
        	//상품 상세 조회 실패
            return RespDto.<ProductDetailRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    //상품별리뷰조회
	@Transactional(readOnly = true)
	public RespDto<ProductReviewRespDto> getProductReviews(Integer productCode, Pageable pageable) {
	    try {
	        // 상품 존재 여부 확인
	        Optional<Product> productOpt = productRepository.findById(productCode);
	        if (productOpt.isEmpty()) {
	            return RespDto.<ProductReviewRespDto>builder()
	                    .code(-1)
	                    .data(null)
	                    .build();
	        }
	        
	        // 상품 리뷰 조회 (페이징)
	        Page<UserReview> reviewPage = userReviewRepository.findByProductIdOrderByCreatedAtDesc(productCode, pageable);
	        
	        if (reviewPage.isEmpty()) {
	            return RespDto.<ProductReviewRespDto>builder()
	                    .code(1)
	                    .data(ProductReviewRespDto.builder()
	                            .items(List.of())
	                            .pagination(ProductReviewRespDto.PaginationDto.from(reviewPage))
	                            .build())
	                    .build();
	        }
	        
	        // 리뷰 ID 목록 추출
	        List<Integer> reviewIds = reviewPage.getContent().stream()
	                .map(UserReview::getReviewId)
	                .collect(Collectors.toList());
	        
	        // 리뷰 이미지들 조회 (한 번에 조회하여 N+1 문제 해결)
	        List<ReviewImg> reviewImgs = reviewImgRepository.findByReviewIdInOrderByReviewIdAndCreatedAt(reviewIds);
	        
	        // 리뷰 ID별로 이미지 그룹화
	        Map<Integer, List<String>> reviewImgMap = reviewImgs.stream()
	                .collect(Collectors.groupingBy(
	                        ReviewImg::getReviewId,
	                        Collectors.mapping(ReviewImg::getReviewImgUrl, Collectors.toList())
	                ));
	        
	        // ReviewItemDto 리스트 생성
	        List<ProductReviewRespDto.ReviewItemDto> reviewItems = reviewPage.getContent().stream()
	                .map(review -> {
	                    // 해당 리뷰의 이미지 URL 리스트
	                    List<String> reviewImgUrls = reviewImgMap.getOrDefault(review.getReviewId(), List.of());
	                    
	                    // 사용자 이름 가져오기
	                    String userName = review.getUser().getName();
	                    
	                    return ProductReviewRespDto.ReviewItemDto.from(review, userName, reviewImgUrls);
	                })
	                .collect(Collectors.toList());
	        
	        ProductReviewRespDto productReviewRespDto = ProductReviewRespDto.from(reviewItems, reviewPage);
	        
	        //상품 리뷰 조회 성공
	        return RespDto.<ProductReviewRespDto>builder()
	                .code(1)
	                .data(productReviewRespDto)
	                .build();
	                
	    } catch (Exception e) {
	    	//상품 리뷰 조회 실패
	        return RespDto.<ProductReviewRespDto>builder()
	                .code(-1)
	                .data(null)
	                .build();
	    }
	}
	
	
    /**
     * 상품 목록에 대한 이미지 맵을 생성하는 헬퍼 메서드
     * N+1 문제를 해결하기 위해 배치로 이미지를 조회
     */
    private Map<Integer, List<String>> getProductImagesMap(List<Product> products) {
        if (products.isEmpty()) {
            return Map.of();
        }
        
        // 상품 ID 목록 추출
        List<Integer> productIds = products.stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());
        
        // 상품 이미지들 배치 조회 (N+1 문제 해결)
        List<ProductImg> productImgs = productImgRepository.findByProductIdInOrderByProductIdAndCreatedAt(productIds);
        
        // 상품 ID별로 이미지 URL 그룹화
        return productImgs.stream()
                .collect(Collectors.groupingBy(
                        img -> img.getProduct().getProductId(),
                        Collectors.mapping(ProductImg::getProductImgUrl, Collectors.toList())
                ));
    }
}