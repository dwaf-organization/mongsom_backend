package com.mongsom.dev.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.product.respDto.ProductDetailRespDto;
import com.mongsom.dev.dto.product.respDto.ProductListRespDto;
import com.mongsom.dev.dto.product.respDto.ProductReviewRespDto;
import com.mongsom.dev.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    
    private final ProductService productService;
    //전체상품조회
    @GetMapping("/all/{page}")
    public ResponseEntity<RespDto<ProductListRespDto>> getAllProducts(
            @PathVariable("page") Integer page, @RequestParam(value = "size", defaultValue = "9") Integer size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        
        RespDto<ProductListRespDto> response = productService.getAllProducts(pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //프리미엄상품조회
    @GetMapping("/premium/{page}")
    public ResponseEntity<RespDto<ProductListRespDto>> getPremiumProducts(
            @PathVariable("page") Integer page, @RequestParam(value = "size", defaultValue = "9") Integer size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        
        RespDto<ProductListRespDto> response = productService.getPremiumProducts(pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    @GetMapping("/popular/{page}/{size}")
    public ResponseEntity<RespDto<ProductListRespDto>> getPopularProducts(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        
        log.info("인기 상품 조회 요청 - page: {}, size: {}", page, size);
        
        // 페이지 번호 검증 (1-based를 0-based로 변환)
        if (page < 1) {
            page = 1;
        }
        
        // 사이즈 검증
        if (size < 1) {
            size = 9;
        }
        
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page - 1, size);
        
        RespDto<ProductListRespDto> response = productService.getPopularProducts(pageable);
        return ResponseEntity.ok(response);
    }
    
    //최신상품조회
    @GetMapping("/new/{page}")
    public ResponseEntity<RespDto<ProductListRespDto>> getNewProducts(
            @PathVariable("page") Integer page, @RequestParam(value = "size", defaultValue = "9") Integer size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        
        RespDto<ProductListRespDto> response = productService.getNewProducts(pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //상품리뷰순조회
    @GetMapping("/review/{page}")
    public ResponseEntity<RespDto<ProductListRespDto>> getProductsByReviewCount(
            @PathVariable("page") Integer page, @RequestParam(value = "size", defaultValue = "9") Integer size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        
        RespDto<ProductListRespDto> response = productService.getProductsByReviewCount(pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //상품상세조회
    @GetMapping("/{productCode}")
    public ResponseEntity<RespDto<ProductDetailRespDto>> getProductDetail(
    		@PathVariable("productCode") Integer productCode) {
        
        RespDto<ProductDetailRespDto> response = productService.getProductDetail(productCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //상품별리뷰조회
    @GetMapping("/review/{productCode}/{page}")
    public ResponseEntity<RespDto<ProductReviewRespDto>> getProductReviews(
            @PathVariable("productCode") Integer productCode,
            @PathVariable("page") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        
        RespDto<ProductReviewRespDto> response = productService.getProductReviews(productCode, pageable);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}