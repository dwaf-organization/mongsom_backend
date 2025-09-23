package com.mongsom.dev.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.AdminProductUpdateReqDto;
import com.mongsom.dev.dto.admin.product.reqDto.ChangeApprovalReqDto;
import com.mongsom.dev.dto.admin.product.reqDto.ProductRegistReqDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductDetailRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductListRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminProductSelectRespDto;
import com.mongsom.dev.dto.admin.product.respDto.ChangeProductListRespDto;
import com.mongsom.dev.service.admin.AdminProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin/product")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {
    
    private final AdminProductService adminProductService;
    //상품등록
    @PostMapping("/regist")
    public ResponseEntity<RespDto<Boolean>> registProduct(@Valid @RequestBody ProductRegistReqDto reqDto) {
        log.info("상품 등록 요청 - name: {}, optionCount: {}, imageCount: {}", 
                reqDto.getName(), 
                reqDto.getOptNames() != null ? reqDto.getOptNames().size() : 0,
                reqDto.getProductImgUrls() != null ? reqDto.getProductImgUrls().size() : 0);
        
        RespDto<Boolean> response = adminProductService.registProduct(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //상품목록조회
    @GetMapping("/select/list/{page}")
    public ResponseEntity<RespDto<AdminProductListRespDto>> selectProducts(
            @PathVariable("page") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "premium", defaultValue = "2") Integer premium) {
        
        log.info("관리자 상품 조회 요청 - page: {}, size: {}, name: {}, premium: {}", 
                page, size, name, premium);
        
        // 페이지 번호 검증 (1-based를 0-based로 변환)
        if (page < 1) {
            page = 1;
        }
        
        RespDto<AdminProductListRespDto> response = 
        		adminProductService.selectProducts(page - 1, size, name, premium);
        
        return ResponseEntity.ok(response);
    }
    //상품상세조회
    @GetMapping("/select/detail/{productId}")
    public ResponseEntity<RespDto<AdminProductDetailRespDto>> selectProductDetail(
            @PathVariable("productId") Integer productId) {
        
        log.info("관리자 상품 상세조회 요청 - productId: {}", productId);
        
        RespDto<AdminProductDetailRespDto> response = 
        		adminProductService.selectProductDetail(productId);
        
        return ResponseEntity.ok(response);
    }
    //상품상세수정
    @PutMapping("/update/{productId}")
    public ResponseEntity<RespDto<Boolean>> updateProduct(
            @PathVariable("productId") Integer productId,
            @Valid @RequestBody AdminProductUpdateReqDto reqDto) {
        
        log.info("관리자 상품 수정 요청 - productId: {}, name: {}", productId, reqDto.getName());
        
        RespDto<Boolean> response = adminProductService.updateProduct(productId, reqDto);
        
        return ResponseEntity.ok(response);
    }
    //상품삭제
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<RespDto<Boolean>> deleteProduct(@PathVariable("productId") Integer productId) {
        
        log.info("관리자 상품 삭제 요청 - productId: {}", productId);
        
        RespDto<Boolean> response = adminProductService.deleteProduct(productId);
        
        return ResponseEntity.ok(response);
    }
    
    // 교환/반품 상품 목록 조회
    @GetMapping("/change/list/{changeStatus}/{page}")
    public ResponseEntity<RespDto<ChangeProductListRespDto>> getChangeProductList(
            @PathVariable("changeStatus") Integer changeStatus,
            @PathVariable("page") Integer page) {

        log.info("교환/반품 상품 목록 조회 요청 - changeStatus: {}, page: {}", changeStatus, page);

        RespDto<ChangeProductListRespDto> response = adminProductService.getChangeProductList(changeStatus, page);

        return ResponseEntity.ok(response);
    }
    
    // 교환/반품 승인/반려 처리
    @PutMapping("/change/update")
    public ResponseEntity<RespDto<Boolean>> updateChangeApproval(
            @RequestBody ChangeApprovalReqDto reqDto) {

        log.info("교환/반품 승인/반려 처리 요청 - changeId: {}, approvalStatus: {}", 
                reqDto.getChangeId(), reqDto.getApprovalStatus());

        RespDto<Boolean> response = adminProductService.updateChangeApproval(reqDto);

        return ResponseEntity.ok(response);
    }
}