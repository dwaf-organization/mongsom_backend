package com.mongsom.dev.service.admin;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.AdminDeliveryUpdateReqDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminOrderDetailRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminOrderListRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminOrderListWithPagingRespDto;
import com.mongsom.dev.entity.OrderDetail;
import com.mongsom.dev.entity.OrderItem;
import com.mongsom.dev.entity.Payments;
import com.mongsom.dev.entity.Product;
import com.mongsom.dev.repository.ChangeItemRepository;
import com.mongsom.dev.repository.OrderDetailRepository;
import com.mongsom.dev.repository.OrderItemRepository;
import com.mongsom.dev.repository.PaymentsRepository;
import com.mongsom.dev.repository.ProductImgRepository;
import com.mongsom.dev.repository.ProductOptionRepository;
import com.mongsom.dev.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminOrderService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductImgRepository productImgRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentsRepository paymentsRepository;
    private final ChangeItemRepository changeItemRepository;
    
    // 관리자 주문 조회 (페이징)
    public RespDto<AdminOrderListWithPagingRespDto> getOrderList(Integer page, Integer size, String startDate, String endDate, String orderId) {
        try {
            log.info("=== 관리자 주문 조회 시작 - page: {}, startDate: {}, endDate: {}, orderId: {} ===", 
                    page, startDate, endDate, orderId);
            
            // 1. 조건에 따른 주문 조회 (페이징) - 사이즈 5 고정, page를 0-based로 변환
            Page<OrderItem> orderItemPage = getOrdersByConditionWithPaging(page - 1, 5, startDate, endDate, orderId);
            
            // 2. DTO 변환
            List<AdminOrderListRespDto> orderDtos = orderItemPage.getContent().stream()
                    .map(this::convertToAdminOrderDto)
                    .collect(Collectors.toList());
            
            // 3. 페이징 정보 생성
            AdminOrderListWithPagingRespDto responseWithPaging = AdminOrderListWithPagingRespDto.builder()
                    .orders(orderDtos)
                    .pagination(AdminOrderListWithPagingRespDto.PaginationDto.builder()
                            .currentPage(page) // 1-based 페이지
                            .totalPage(orderItemPage.getTotalPages())
                            .size(5) // 페이지 크기 5 고정
                            .hasNext(orderItemPage.hasNext())
                            .build())
                    .build();
            
            log.info("=== 관리자 주문 조회 완료 - 조회된 주문 수: {}, 총 페이지: {}, 전체 주문 수: {} ===", 
                    orderDtos.size(), orderItemPage.getTotalPages(), orderItemPage.getTotalElements());
            
            return RespDto.<AdminOrderListWithPagingRespDto>builder()
                    .code(1)
                    .data(responseWithPaging)
                    .build();
                    
        } catch (Exception e) {
            log.error("관리자 주문 조회 실패 - page: {}, size: {}, startDate: {}, endDate: {}, orderId: {}, error: {}", 
                    page, size, startDate, endDate, orderId, e.getMessage(), e);
            return RespDto.<AdminOrderListWithPagingRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    // 조건에 따른 주문 조회 (페이징) (상품목록조회용)
    private Page<OrderItem> getOrdersByConditionWithPaging(Integer page, Integer size, String startDate, String endDate, String orderId) {
        // Pageable 객체 생성
        Pageable pageable = PageRequest.of(page, size);
        
        // 문자열 날짜를 LocalDate로 변환
        LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        if (orderId != null && !orderId.trim().isEmpty()) {
            // 주문번호 포함 검색 + 날짜 범위 + 페이징
            return orderItemRepository.findByOrderIdContainingAndPaymentAtBetweenWithPaging(
                    orderId.trim(), startLocalDate, endLocalDate, pageable);
        } else {
            // 날짜 범위만 + 페이징
            return orderItemRepository.findByPaymentAtBetweenWithPaging(startLocalDate, endLocalDate, pageable);
        }
    }
    
    //OrderItem을 AdminOrderListRespDto로 변환 (상품목록조회용)
    private AdminOrderListRespDto convertToAdminOrderDto(OrderItem orderItem) {
        try {
            // 주문 상세 정보 조회
            List<AdminOrderListRespDto.OrderDetailDto> orderDetails = 
                    getOrderDetailsByOrderId(orderItem.getOrderId());
            
            return AdminOrderListRespDto.builder()
                    .orderId(orderItem.getOrderId())
                    .userCode(orderItem.getUserCode())
                    .receivedUserName(orderItem.getReceivedUserName())
                    .finalPrice(orderItem.getFinalPrice())
                    .deliveryStatus(orderItem.getDeliveryStatus())
                    .deliveryCom(orderItem.getDeliveryCom())
                    .invoiceNum(orderItem.getInvoiceNum())
                    .changeState(orderItem.getChangeState())
                    .paymentAt(orderItem.getPaymentAt())
                    .orderDetails(orderDetails)
                    .build();
                    
        } catch (Exception e) {
            log.error("주문 DTO 변환 실패 - orderId: {}, error: {}", orderItem.getOrderId(), e.getMessage());
            return null;
        }
    }
    
    // 주문 상세 정보 조회 (상품목록조회용)
    private List<AdminOrderListRespDto.OrderDetailDto> getOrderDetailsByOrderId(Integer orderId) {
        try {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
            
            return orderDetails.stream()
                    .map(detail -> {
                        // 상품 정보 조회
                        String productName = getProductName(detail.getProductId());
                        List<String> productImgUrls = getProductImages(detail.getProductId());
                        
                        return AdminOrderListRespDto.OrderDetailDto.builder()
                                .orderDetailId(detail.getOrderDetailId())
                                .productId(detail.getProductId())
                                .productName(productName)
                                .productImgUrls(productImgUrls)
                                .build();
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("주문 상세 정보 조회 실패 - orderId: {}, error: {}", orderId, e.getMessage());
            return List.of();
        }
    }
    
    // 상품명 조회 (상품목록조회용)
    private String getProductName(Integer productId) {
        try {
            return productRepository.findById(productId)
                    .map(Product::getName)
                    .orElse("상품명 없음");
        } catch (Exception e) {
            log.error("상품명 조회 실패 - productId: {}", productId);
            return "상품명 조회 실패";
        }
    }
    
    // 상품 이미지 조회 (상품목록조회용)
    private List<String> getProductImages(Integer productId) {
        try {
            List<String> images = productImgRepository.findImgUrlsByProductId(productId);
            return images != null ? images : List.of();
        } catch (Exception e) {
            log.error("상품 이미지 조회 실패 - productId: {}", productId);
            return List.of();
        }
    }
    
    // 관리자 주문 상세 조회
    public RespDto<AdminOrderDetailRespDto> getOrderDetail(Integer orderId) {
        try {
            log.info("=== 관리자 주문 상세 조회 시작 - orderId: {} ===", orderId);
            
            // 1. 주문 기본 정보 조회
            OrderItem orderItem = orderItemRepository.findById(orderId).orElse(null);
            if (orderItem == null) {
                log.warn("주문 정보를 찾을 수 없음 - orderId: {}", orderId);
                return RespDto.<AdminOrderDetailRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            // 2. 결제 정보 조회
            Payments paymentInfo = getPaymentInfo(orderId);
            
            // 3. 주문 상세 정보 조회
            List<AdminOrderDetailRespDto.OrderDetailDto> orderDetails = getAdminOrderDetailsByOrderId(orderId);
            
            // 4. 응답 DTO 생성
            AdminOrderDetailRespDto respDto = AdminOrderDetailRespDto.builder()
                    .orderId(orderItem.getOrderId())
                    .paymentAt(orderItem.getPaymentAt())
                    .deliveryStatus(orderItem.getDeliveryStatus())
                    .finalPrice(orderItem.getFinalPrice())
                    .userCode(orderItem.getUserCode())
                    .receivedUserName(orderItem.getReceivedUserName())
                    .receivedUserPhone(orderItem.getReceivedUserPhone())
                    .receivedUserZipCode(orderItem.getReceivedUserZipCode())
                    .receivedUserAddress(orderItem.getReceivedUserAddress())
                    .receivedUserAddress2(orderItem.getReceivedUserAddress2())
                    .message(orderItem.getMessage())
                    .changeState(orderItem.getChangeState())
                    .deliveryCom(orderItem.getDeliveryCom())    // 추가
                    .invoiceNum(orderItem.getInvoiceNum())      // 추가
                    .paymentMethod(paymentInfo != null ? (String) paymentInfo.getPaymentMethod() : null)
                    .paymentAmount(paymentInfo != null ? (Integer) paymentInfo.getPaymentAmount() : null)
                    .paymentStatus(paymentInfo != null ? (String) paymentInfo.getPaymentStatus() : null)
                    .pgProvider(paymentInfo != null ? (String) paymentInfo.getPgProvider() : null)
                    .details(orderDetails)
                    .build();
            
            log.info("=== 관리자 주문 상세 조회 완료 - orderId: {}, 상품 개수: {} ===", orderId, orderDetails.size());
            
            return RespDto.<AdminOrderDetailRespDto>builder()
                    .code(1)
                    .data(respDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("관리자 주문 상세 조회 실패 - orderId: {}, error: {}", orderId, e.getMessage(), e);
            return RespDto.<AdminOrderDetailRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    // 결제 정보 조회 (주문상세조회용)
    private Payments getPaymentInfo(Integer orderId) {
        try {
        	Optional<Payments> paymentInfoList = paymentsRepository.findPaymentInfoByOrderId(orderId);
            return paymentInfoList.isEmpty() ? null : paymentInfoList.get();
        } catch (Exception e) {
            log.error("결제 정보 조회 실패 - orderId: {}", orderId);
            return null;
        }
    }
    
    // 관리자용 주문 상세 정보 조회 (옵션명 포함) (주문상세조회용)
    private List<AdminOrderDetailRespDto.OrderDetailDto> getAdminOrderDetailsByOrderId(Integer orderId) {
        try {
            List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
            List<Integer> orderDetailIds = orderDetails.stream()
                    .map(OrderDetail::getOrderDetailId)
                    .collect(Collectors.toList());
            
            // change_item 테이블에서 교환/반품 상태 조회
            Map<Integer, Integer> changeStatusMap = getChangeStatusMap(orderDetailIds);
            
            return orderDetails.stream()
                    .map(detail -> {
                        String productName = getProductName(detail.getProductId());
                        List<String> productImgUrls = getProductImages(detail.getProductId());
                        String optName = getOptionName(detail.getOptId());
                        Integer changeStatus = changeStatusMap.get(detail.getOrderDetailId());
                        
                        return AdminOrderDetailRespDto.OrderDetailDto.builder()
                                .orderDetailId(detail.getOrderDetailId())
                                .productId(detail.getProductId())
                                .productName(productName)
                                .optId(detail.getOptId())
                                .optName(optName)
                                .changeStatus(changeStatus)
                                .productImgUrls(productImgUrls)
                                .quantity(detail.getQuantity())
                                .price(detail.getPrice())
                                .orderStatus(detail.getOrderStatus())
                                .build();
                    })
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("관리자 주문 상세 정보 조회 실패 - orderId: {}", orderId);
            return List.of();
        }
    }
    
    // 교환/반품 상태 조회 (주문상세조회용)
    private Map<Integer, Integer> getChangeStatusMap(List<Integer> orderDetailIds) {
        try {
            if (orderDetailIds.isEmpty()) {
                return Map.of();
            }
            
            List<Object[]> changeStatusList = changeItemRepository.findChangeStatusByOrderDetailIds(orderDetailIds);
            return changeStatusList.stream()
                    .collect(Collectors.toMap(
                            row -> (Integer) row[0], // order_detail_id
                            row -> (Integer) row[1]  // change_status
                    ));
        } catch (Exception e) {
            log.error("교환/반품 상태 조회 실패");
            return Map.of();
        }
    }
    
    // 옵션명 조회 (주문상세조회용)
    private String getOptionName(Integer optId) {
        try {
            return productOptionRepository.findById(optId)
                    .map(option -> option.getOptName())
                    .orElse("옵션명 없음");
        } catch (Exception e) {
            log.error("옵션명 조회 실패 - optId: {}", optId);
            return "옵션명 조회 실패";
        }
    }
    
    /**
     * 배송 정보 업데이트
     * @param reqDto 배송 정보 업데이트 요청 DTO
     * @return 성공/실패 여부
     */
    @Transactional
    public RespDto<Boolean> updateDeliveryInfo(AdminDeliveryUpdateReqDto reqDto) {
        try {
            log.info("=== 배송 정보 업데이트 시작 - orderId: {}, userCode: {} ===", 
                    reqDto.getOrderId(), reqDto.getUserCode());
            
            // 1. 주문 정보 조회 및 검증
            OrderItem orderItem = orderItemRepository.findByOrderIdAndUserCode(
                    reqDto.getOrderId(), reqDto.getUserCode()).orElse(null);
            
            if (orderItem == null) {
                log.warn("주문 정보를 찾을 수 없음 - orderId: {}, userCode: {}", 
                        reqDto.getOrderId(), reqDto.getUserCode());
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 2. 배송 정보 업데이트
            if (reqDto.getDeliveryStatus() != null) {
                orderItem.setDeliveryStatus(reqDto.getDeliveryStatus());
            }
            if (reqDto.getDeliveryCom() != null) {
                orderItem.setDeliveryCom(reqDto.getDeliveryCom());
            }
            if (reqDto.getInvoiceNum() != null) {
                orderItem.setInvoiceNum(reqDto.getInvoiceNum());
            }
            
            // 3. 저장
            orderItemRepository.save(orderItem);
            
            log.info("=== 배송 정보 업데이트 완료 - orderId: {}, deliveryStatus: {}, deliveryCom: {}, invoiceNum: {} ===", 
                    reqDto.getOrderId(), reqDto.getDeliveryStatus(), reqDto.getDeliveryCom(), reqDto.getInvoiceNum());
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("배송 정보 업데이트 실패 - orderId: {}, userCode: {}, error: {}", 
                    reqDto.getOrderId(), reqDto.getUserCode(), e.getMessage(), e);
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
}
