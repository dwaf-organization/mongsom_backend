package com.mongsom.dev.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.payment.reqDto.PaymentConfirmReqDto;
import com.mongsom.dev.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * 토스페이먼츠 결제 승인
     */
    @PostMapping("/confirm")
    public ResponseEntity<RespDto<String>> confirmPayment(
            @RequestBody PaymentConfirmReqDto reqDto) {
        
        log.info("결제 승인 요청 - paymentKey: {}, orderId: {}, amount: {}", 
                reqDto.getPaymentKey(), reqDto.getOrderId(), reqDto.getAmount());
        
        RespDto<String> response = paymentService.confirmPayment(reqDto);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 토스페이먼츠 결제 조회 (paymentKey)
     */
    @GetMapping("/{paymentKey}")
    public ResponseEntity<Map<String, Object>> getPayment(
            @PathVariable("paymentKey") String paymentKey) {
        
        log.info("결제 조회 요청 - paymentKey: {}", paymentKey);
        
        Map<String, Object> response = paymentService.getPaymentByKey(paymentKey);
        
        return ResponseEntity.ok(response);
    }
}