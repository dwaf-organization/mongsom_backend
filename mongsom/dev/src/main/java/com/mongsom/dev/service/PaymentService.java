package com.mongsom.dev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.payment.reqDto.PaymentConfirmReqDto;
import com.mongsom.dev.entity.OrderItem;
import com.mongsom.dev.entity.Payments;
import com.mongsom.dev.repository.OrderItemRepository;
import com.mongsom.dev.repository.PaymentsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OrderItemRepository orderItemRepository;
    private final PaymentsRepository paymentsRepository;
    
    @Value("${toss.secret-key}")
    private String tossSecretKey;
    
    /**
     * 토스페이먼츠 결제 승인
     */
    public RespDto<String> confirmPayment(PaymentConfirmReqDto reqDto) {
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

            Map<String, Object> responseBody = response.getBody();

            // 8. 응답이 성공이면 DB 업데이트 진행
            if (responseBody != null && "DONE".equals(responseBody.get("status"))) {
                log.info("=== DB 업데이트 시작 ===");
                
                String orderNum = (String) responseBody.get("orderId");  // mongsom_100023
                String method = (String) responseBody.get("method");  // "카드"
                Integer totalAmount = (Integer) responseBody.get("totalAmount");  // 68000
                String paymentKey = (String) responseBody.get("paymentKey");  // "tviva20250929143411SGeO6"
                
                log.info("orderNum: {}, method: {}, totalAmount: {}, paymentKey: {}", 
                        orderNum, method, totalAmount, paymentKey);
                
                // 8-1. orderNum으로 주문 조회
                Optional<OrderItem> orderItemOpt = orderItemRepository.findByOrderNum(orderNum);
                
                if (orderItemOpt.isEmpty()) {
                    log.error("주문을 찾을 수 없음 - orderNum: {}", orderNum);
                    return RespDto.<String>builder()
                            .code(-1)
                            .data("주문을 찾을 수 없습니다.")
                            .build();
                }
                
                OrderItem orderItem = orderItemOpt.get();
                Integer orderId = orderItem.getOrderId();
                
                log.info("주문 조회 성공 - orderId: {}, orderNum: {}", orderId, orderNum);
                
                // 8-2. OrderItem 업데이트
                orderItem.setDeliveryStatus("결제완료");
                orderItem.setPaymentAt(LocalDateTime.now());
                orderItemRepository.save(orderItem);
                
                log.info("OrderItem 업데이트 완료 - orderId: {}, deliveryStatus: 결제완료", orderId);
                
                // 8-3. Payments 조회
                List<Payments> paymentList = paymentsRepository.findByOrderId(orderId);
                
                if (paymentList == null || paymentList.isEmpty()) {
                    log.error("결제 정보를 찾을 수 없음 - orderId: {}", orderId);
                    return RespDto.<String>builder()
                            .code(-1)
                            .data("결제 정보를 찾을 수 없습니다.")
                            .build();
                }
                
                // 8-4. Payments 업데이트 (리스트의 모든 결제 정보 업데이트)
                for (Payments payment : paymentList) {
                    payment.setPaymentMethod(method);
                    payment.setPaymentAmount(totalAmount);
                    payment.setPaymentStatus("COMPLETED");
                    payment.setPaymentKey(paymentKey);
                    paymentsRepository.save(payment);
                    
                    log.info("Payments 업데이트 완료 - paymentId: {}, orderId: {}, method: {}, amount: {}, status: COMPLETED", 
                            payment.getPaymentId(), orderId, method, totalAmount);
                }
                
                log.info("=== DB 업데이트 완료 ===");
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("결제가 승인되었습니다.")
                        .build();
                
            } else {
                log.warn("결제 상태가 DONE이 아님 - status: {}", responseBody != null ? responseBody.get("status") : "null");
                return RespDto.<String>builder()
                        .code(-1)
                        .data("결제 승인 실패")
                        .build();
            }

        } catch (HttpClientErrorException e) {
            log.error("토스페이먼츠 API 호출 실패", e);
            log.error("Status Code: {}", e.getStatusCode());
            log.error("Response Body: {}", e.getResponseBodyAsString());
            
            return RespDto.<String>builder()
                    .code(-1)
                    .data("토스페이먼츠 API 호출 실패: " + e.getStatusCode())
                    .build();
                    
        } catch (Exception e) {
            log.error("결제 승인 처리 중 오류 발생", e);
            
            return RespDto.<String>builder()
                    .code(-1)
                    .data("결제 승인 처리 중 오류 발생: " + e.getMessage())
                    .build();
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