package com.mongsom.dev.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.ProductRegistReqDto;
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
}