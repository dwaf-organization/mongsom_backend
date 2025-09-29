package com.mongsom.dev.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.order.reqDto.OrderCancelReqDto;
import com.mongsom.dev.dto.order.reqDto.OrderCreateReqDto;
import com.mongsom.dev.dto.order.reqDto.PaymentUpdateReqDto;
import com.mongsom.dev.entity.OrderDetail;
import com.mongsom.dev.entity.OrderItem;
import com.mongsom.dev.entity.Payments;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.entity.User;
import com.mongsom.dev.repository.CartRepository;
import com.mongsom.dev.repository.OrderDetailRepository;
import com.mongsom.dev.repository.OrderItemRepository;
import com.mongsom.dev.repository.PaymentsRepository;
import com.mongsom.dev.repository.ProductRepository;
import com.mongsom.dev.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentsRepository paymentsRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    
    // 주문생성
    @Transactional
    public RespDto<String> createOrder(OrderCreateReqDto reqDto) {
        try {
            log.info("주문 생성 시작 - userCode: {}, finalPrice: {}, 상품 수: {}", 
                    reqDto.getUserCode(), reqDto.getFinalPrice(), reqDto.getOrderDetails().size());
            
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                log.warn("존재하지 않는 사용자 - userCode: {}", reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            // 2. 상품 존재 확인
            for (OrderCreateReqDto.OrderDetailDto detail : reqDto.getOrderDetails()) {
                Optional<Product> productOpt = productRepository.findById(detail.getProductId());
                if (productOpt.isEmpty()) {
                    log.warn("존재하지 않는 상품 - productId: {}", detail.getProductId());
                    return RespDto.<String>builder()
                            .code(-1)
                            .data(null)
                            .build();
                }
            }
            
            // 3. OrderItem 생성 및 저장
            OrderItem orderItem = OrderItem.builder()
                    .userCode(reqDto.getUserCode())
                    .receivedUserName(reqDto.getReceivedUserName())
                    .receivedUserPhone(reqDto.getReceivedUserPhone())
                    .receivedUserZipCode(reqDto.getReceivedUserZipCode())
                    .receivedUserAddress(reqDto.getReceivedUserAddress())
                    .receivedUserAddress2(reqDto.getReceivedUserAddress2())
                    .message(reqDto.getMessage())
                    .totalPrice(reqDto.getTotalPrice())
                    .deliveryPrice(reqDto.getDeliveryPrice())
                    .totalDiscountPrice(reqDto.getTotalDiscountPrice())
                    .finalPrice(reqDto.getFinalPrice())
                    .deliveryStatus("결제대기")  // 고정값
                    .paymentAt(reqDto.getPaymentAt() != null ? reqDto.getPaymentAt() : LocalDateTime.now())
                    .changeState(0)  // 기본값: 주문
                    .orderNum("temp")
                    .build();
            
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            Integer orderId = savedOrderItem.getOrderId();
            
            // 3-1. orderNum 생성 및 업데이트 (mongsom_orderId 형식)
            String orderNum = "mongsom_" + orderId;
            savedOrderItem.setOrderNum(orderNum);
            orderItemRepository.save(savedOrderItem);
            
            log.info("주문 기본 정보 저장 완료 - orderId: {}", orderId);
            
            // 4. OrderDetail 생성 및 저장
            for (OrderCreateReqDto.OrderDetailDto detailDto : reqDto.getOrderDetails()) {
                OrderDetail orderDetail = OrderDetail.builder()
                        .orderId(orderId)
                        .userCode(reqDto.getUserCode())
                        .optId(detailDto.getOptId())
                        .productId(detailDto.getProductId())
                        .quantity(detailDto.getQuantity())
                        .price(detailDto.getPrice())
                        .reviewStatus(0)  // 고정값: 리뷰 미작성
                        .build();
                
                orderDetailRepository.save(orderDetail);
            }
            
            log.info("주문 상세 정보 저장 완료 - orderId: {}, 상품 수: {}", orderId, reqDto.getOrderDetails().size());
            
            // 5. Payments 생성 및 저장
            Payments payment = Payments.builder()
                    .orderId(orderId)
                    .userCode(reqDto.getUserCode())
                    .paymentMethod(reqDto.getPaymentMethod())
                    .paymentAmount(reqDto.getPaymentAmount())
                    .paymentStatus(reqDto.getPaymentStatus() != null ? reqDto.getPaymentStatus() : "COMPLETED")
                    .paymentKey(reqDto.getPaymentKey())
                    .pgProvider(reqDto.getPgProvider())
                    .build();
            
            paymentsRepository.save(payment);
            
            log.info("결제 정보 저장 완료 - orderId: {}, paymentMethod: {}, paymentAmount: {}", 
                    orderId, reqDto.getPaymentMethod(), reqDto.getPaymentAmount());
            
            log.info("주문 생성 완료 - orderId: {}, userCode: {}, finalPrice: {}", 
                    orderId, reqDto.getUserCode(), reqDto.getFinalPrice());
            
            return RespDto.<String>builder()
                    .code(1)
                    .data(orderNum)
                    .build();
            
        } catch (Exception e) {
            log.error("주문 생성 실패 - userCode: {}, finalPrice: {}", 
                    reqDto.getUserCode(), reqDto.getFinalPrice(), e);
            return RespDto.<String>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
   
    // 주문취소
    @Transactional
    public RespDto<Boolean> cancelOrder(OrderCancelReqDto reqDto) {
        try {
            log.info("주문 취소 시작 - orderId: {}, userCode: {}, orderDetailId: {}", 
                    reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getOrderDetailId());
            
            // 1. 사용자 존재 확인
            Optional<User> userOpt = userRepository.findUserByUserCode(reqDto.getUserCode());
            if (userOpt.isEmpty()) {
                log.warn("존재하지 않는 사용자 - userCode: {}", reqDto.getUserCode());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 2. 주문 상세 존재 확인 및 권한 검증
            Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(reqDto.getOrderDetailId());
            if (orderDetailOpt.isEmpty()) {
                log.warn("존재하지 않는 주문 상세 - orderDetailId: {}", reqDto.getOrderDetailId());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            OrderDetail orderDetail = orderDetailOpt.get();
            
            // 3. 권한 검증 (주문자 본인인지 확인)
            if (!orderDetail.getUserCode().equals(reqDto.getUserCode()) || 
                !orderDetail.getOrderId().equals(reqDto.getOrderId())) {
                log.warn("주문 취소 권한 없음 - userCode: {}, orderId: {}, orderDetailId: {}", 
                        reqDto.getUserCode(), reqDto.getOrderId(), reqDto.getOrderDetailId());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 4. 이미 취소된 주문인지 확인
            if (orderDetail.getOrderStatus().equals(1)) {
                log.warn("이미 취소된 주문 - orderDetailId: {}", reqDto.getOrderDetailId());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 5. 주문 상세 취소 처리 (order_status = 1)
            orderDetail.setOrderStatus(1);
            orderDetailRepository.save(orderDetail);
            
            log.info("주문 상세 취소 완료 - orderDetailId: {}", reqDto.getOrderDetailId());
            
            // 6. 해당 주문의 모든 상세 조회
            List<OrderDetail> allOrderDetails = orderDetailRepository.findByOrderIdOrderByCreatedAt(reqDto.getOrderId());
            
            // 7. 모든 상세가 취소되었는지 확인
            boolean allCancelled = allOrderDetails.stream()
                    .allMatch(detail -> detail.getOrderStatus().equals(1));
            
            if (allCancelled) {
                // 8. 모든 상세가 취소된 경우 주문 기본 정보의 배송 상태 변경
                Optional<OrderItem> orderItemOpt = orderItemRepository.findById(reqDto.getOrderId());
                if (orderItemOpt.isPresent()) {
                    OrderItem orderItem = orderItemOpt.get();
                    orderItem.setDeliveryStatus("주문취소");
                    orderItemRepository.save(orderItem);
                    
                    log.info("주문 전체 취소 완료 - orderId: {}, deliveryStatus: '주문취소'", reqDto.getOrderId());
                }
            } else {
                log.info("주문 부분 취소 완료 - orderId: {}, 취소된 상세: {}, 전체 상세: {}", 
                        reqDto.getOrderId(), 
                        allOrderDetails.stream().mapToInt(detail -> detail.getOrderStatus()).sum(),
                        allOrderDetails.size());
            }
            
            log.info("주문 취소 처리 완료 - orderId: {}, userCode: {}, orderDetailId: {}", 
                    reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getOrderDetailId());
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
            
        } catch (Exception e) {
            log.error("주문 취소 실패 - orderId: {}, userCode: {}, orderDetailId: {}", 
                    reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getOrderDetailId(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    //결제 정보 업데이트
    @Transactional
    public RespDto<String> updatePaymentInfo(PaymentUpdateReqDto reqDto) {
        try {
            log.info("=== 결제 정보 업데이트 시작 - orderId: {}, userCode: {}, paymentMethod: {} ===", 
                    reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getPaymentMethod());
            
            // 1. 주문 존재 확인
            Optional<OrderItem> orderOpt = orderItemRepository.findById(reqDto.getOrderId());
            if (orderOpt.isEmpty()) {
                log.error("주문을 찾을 수 없습니다 - orderId: {}", reqDto.getOrderId());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("주문을 찾을 수 없습니다.")
                        .build();
            }
            
            OrderItem order = orderOpt.get();
            
            // 2. 사용자 권한 확인
            if (!order.getUserCode().equals(reqDto.getUserCode())) {
                log.error("주문 소유자가 아닙니다 - orderId: {}, requestUserCode: {}, orderUserCode: {}", 
                        reqDto.getOrderId(), reqDto.getUserCode(), order.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("해당 주문에 대한 권한이 없습니다.")
                        .build();
            }
            
            // 3. 기존 결제 정보 조회
            Optional<Payments> paymentOpt = paymentsRepository.findByOrderIdAndUserCode(
                    reqDto.getOrderId(), reqDto.getUserCode());
            
            if (paymentOpt.isEmpty()) {
                log.error("결제 정보를 찾을 수 없습니다 - orderId: {}, userCode: {}", 
                        reqDto.getOrderId(), reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("결제 정보를 찾을 수 없습니다.")
                        .build();
            }
            
            // 4. 결제 정보 업데이트
            int updatedRows = paymentsRepository.updatePaymentInfo(
                    reqDto.getOrderId(),
                    reqDto.getUserCode(),
                    reqDto.getPaymentMethod(),
                    reqDto.getPaymentStatus() != null ? reqDto.getPaymentStatus() : "PENDING",
                    reqDto.getPaymentKey(),
                    reqDto.getPgProvider()
            );
            
            // 4-1. 결제 완료 시 주문 상태 업데이트
            if ("COMPLETED".equals(reqDto.getPaymentStatus()) && updatedRows > 0) {
                int orderUpdatedRows = orderItemRepository.updateDeliveryStatus(
                    reqDto.getOrderId(), "결제완료");
                
                log.info("주문 상태 업데이트 완료 - orderId: {}, deliveryStatus: 결제완료, 업데이트된 행: {}", 
                    reqDto.getOrderId(), orderUpdatedRows);
            }
            
            // 5. 결제 완료 시 장바구니 삭제
            if ("COMPLETED".equals(reqDto.getPaymentStatus())) {
                int deletedCartCount = cartRepository.deleteByUserCode(reqDto.getUserCode());
                log.info("장바구니 삭제 완료 - userCode: {}, 삭제된 항목 수: {}", 
                        reqDto.getUserCode(), deletedCartCount);
            }
            
            if (updatedRows > 0) {
                log.info("결제 정보 업데이트 완료 - orderId: {}, userCode: {}, paymentMethod: {}, paymentStatus: {}", 
                        reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getPaymentMethod(), 
                        reqDto.getPaymentStatus());
                
                return RespDto.<String>builder()
                        .code(1)
                        .data("결제 정보가 성공적으로 업데이트되었습니다.")
                        .build();
            } else {
                log.error("결제 정보 업데이트 실패 - orderId: {}, userCode: {}", 
                        reqDto.getOrderId(), reqDto.getUserCode());
                return RespDto.<String>builder()
                        .code(-1)
                        .data("결제 정보 업데이트에 실패했습니다.")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("결제 정보 업데이트 실패 - orderId: {}, userCode: {}, error: {}", 
                    reqDto.getOrderId(), reqDto.getUserCode(), e.getMessage());
            return RespDto.<String>builder()
                    .code(-1)
                    .data("결제 정보 업데이트 중 오류가 발생했습니다.")
                    .build();
        }
    }
    
}