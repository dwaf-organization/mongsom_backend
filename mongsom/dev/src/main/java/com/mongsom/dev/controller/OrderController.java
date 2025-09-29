package com.mongsom.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.order.reqDto.OrderCancelReqDto;
import com.mongsom.dev.dto.order.reqDto.OrderCreateReqDto;
import com.mongsom.dev.dto.order.reqDto.PaymentUpdateReqDto;
import com.mongsom.dev.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping("/create")
    public ResponseEntity<RespDto<String>> createOrder(@Valid @RequestBody OrderCreateReqDto reqDto) {
        
        log.info("주문 생성 요청 - userCode: {}, finalPrice: {}, 결제수단: {}, 상품 수: {}", 
                reqDto.getUserCode(), reqDto.getFinalPrice(), reqDto.getPaymentMethod(), 
                reqDto.getOrderDetails().size());
        
        RespDto<String> response = orderService.createOrder(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<RespDto<Boolean>> cancelOrder(@Valid @RequestBody OrderCancelReqDto reqDto) {
        
        log.info("주문 취소 요청 - orderId: {}, userCode: {}, orderDetailId: {}", 
                reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getOrderDetailId());
        
        RespDto<Boolean> response = orderService.cancelOrder(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        
        return ResponseEntity.status(status).body(response);
    }
    
    // 결제 정보 업데이트
    @PutMapping("/payment/update")
    public ResponseEntity<RespDto<String>> updatePaymentInfo(@Valid @RequestBody PaymentUpdateReqDto reqDto) {
        
        log.info("결제 정보 업데이트 요청 - orderId: {}, userCode: {}, paymentMethod: {}, paymentStatus: {}", 
                reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getPaymentMethod(), reqDto.getPaymentStatus());
        
        RespDto<String> response = orderService.updatePaymentInfo(reqDto);
        
        if (response.getCode() == 1) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}