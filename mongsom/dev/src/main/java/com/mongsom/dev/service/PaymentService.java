package com.mongsom.dev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongsom.dev.dto.payment.reqDto.PaymentConfirmReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${toss.secret-key}")
    private String tossSecretKey;
    
    /**
     * 토스페이먼츠 결제 승인
     */
    public Map<String, Object> confirmPayment(PaymentConfirmReqDto reqDto) {
        try {
            log.info("=== 토스페이먼츠 결제 승인 요청 시작 ===");
            log.info("paymentKey: {}, orderId: {}, amount: {}", 
                    reqDto.getPaymentKey(), reqDto.getOrderId(), reqDto.getAmount());
            
            // 1. 토스페이먼츠 API 엔드포인트
            String url = "https://api.tosspayments.com/v1/payments/confirm";
            
            // 2. 인증 헤더 생성 (Secret Key를 Base64 인코딩)
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((tossSecretKey + ":").getBytes());
            
            // 3. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + encodedAuth);
            
            // 4. 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("paymentKey", reqDto.getPaymentKey());
            requestBody.put("orderId", reqDto.getOrderId());
            requestBody.put("amount", reqDto.getAmount());
            
            // 5. HTTP 요청 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 6. 토스페이먼츠 API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            // 7. 응답 로깅
            log.info("=== 토스페이먼츠 응답 ===");
            log.info("Status Code: {}", response.getStatusCode());
            log.info("Response Body: {}", objectMapper.writeValueAsString(response.getBody()));
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("토스페이먼츠 결제 승인 실패", e);
            
            // 상세 오류 정보 출력
            if (e instanceof HttpClientErrorException) {
                HttpClientErrorException httpError = (HttpClientErrorException) e;
                log.error("Status Code: {}", httpError.getStatusCode());
                log.error("Response Body: {}", httpError.getResponseBodyAsString());
            }
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            
            return errorResponse;
        }
    }
    
    /**
     * 토스페이먼츠 결제 조회 (paymentKey 사용)
     */
    public Map<String, Object> getPaymentByKey(String paymentKey) {
        try {
            log.info("=== 토스페이먼츠 결제 조회 요청 시작 ===");
            log.info("paymentKey: {}", paymentKey);
            
            // 1. 토스페이먼츠 API 엔드포인트
            String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;
            
            // 2. 인증 헤더 생성 (Secret Key를 Base64 인코딩)
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((tossSecretKey + ":").getBytes());
            
            // 3. 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + encodedAuth);
            
            // 4. HTTP 요청 생성
            HttpEntity<Void> request = new HttpEntity<>(headers);
            
            // 5. 토스페이먼츠 API 호출 (GET)
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, 
                    HttpMethod.GET, 
                    request, 
                    Map.class
            );
            
            // 6. 응답 로깅
            log.info("=== 토스페이먼츠 결제 조회 응답 ===");
            log.info("Status Code: {}", response.getStatusCode());
            log.info("Response Body: {}", objectMapper.writeValueAsString(response.getBody()));
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("토스페이먼츠 결제 조회 실패", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", e.getMessage());
            
            return errorResponse;
        }
    }
}