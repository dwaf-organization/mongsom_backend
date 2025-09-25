package com.mongsom.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.cart.reqDto.CartAddReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartAllCheckReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartChangeCheckReqDto;
import com.mongsom.dev.dto.cart.reqDto.CartUpdateQuantityReqDto;
import com.mongsom.dev.dto.cart.respDto.CartRespDto;
import com.mongsom.dev.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    
    private final CartService cartService;
    
    //장바구니조회
    @GetMapping("/{userCode}")
    public ResponseEntity<RespDto<CartRespDto>> getCartItems(
            @PathVariable("userCode") Integer userCode) {
        RespDto<CartRespDto> response = cartService.getCartItems(userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //장바구니추가
    @PostMapping("/add")
    public ResponseEntity<RespDto<String>> addToCart(
            @Valid @RequestBody CartAddReqDto reqDto) {
        RespDto<String> response = cartService.addToCart(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //장바구니상품삭제
    @DeleteMapping("/delete/{userCode}/{productId}/{optId}")
    public ResponseEntity<RespDto<String>> deleteCartItem(
            @PathVariable("userCode") Integer userCode,
            @PathVariable("productId") Integer productId,
            @PathVariable("optId") Integer optId) {
        RespDto<String> response = cartService.deleteCartItem(userCode, productId, optId);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //장바구니수량변경
    @PutMapping("/update/quantity")
    public ResponseEntity<RespDto<String>> updateCartQuantity(
            @Valid @RequestBody CartUpdateQuantityReqDto reqDto) {
        RespDto<String> response = cartService.updateCartQuantity(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    //장바구니체크변경
    @PutMapping("/change/check")
    public ResponseEntity<RespDto<String>> changeCheckStatus(
            @Valid @RequestBody CartChangeCheckReqDto reqDto) {
        RespDto<String> response = cartService.changeCheckStatus(reqDto);
        return ResponseEntity.ok(response);
    }
    // 장바구니 전체 체크 상태 변경
    @PutMapping("/change/check/all")
    public ResponseEntity<RespDto<String>> updateAllCheckStatus(@Valid @RequestBody CartAllCheckReqDto reqDto) {
        
        String statusMessage = (reqDto.getAllCheckStatus() == 1) ? "전체 선택" : "전체 해제";
        log.info("장바구니 전체 체크 상태 변경 요청 - userCode: {}, 상태: {}", 
                reqDto.getUserCode(), statusMessage);
        
        RespDto<String> response = cartService.updateAllCheckStatus(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}