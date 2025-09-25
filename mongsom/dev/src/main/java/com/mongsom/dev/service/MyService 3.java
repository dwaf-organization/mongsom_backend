package com.mongsom.dev.service;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.mypage.reqDto.ChangeCreateReqDto;
import com.mongsom.dev.dto.mypage.reqDto.ChangeDeleteReqDto;
import com.mongsom.dev.dto.mypage.respDto.DeliveryRespDto;
import com.mongsom.dev.dto.mypage.respDto.DeliveryStatusRespDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderDetailRespDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderDetailRespDto.MyOrderDetailItemDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderRespDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderRespDto.MyOrderDetailDto;
import com.mongsom.dev.dto.review.reqDto.ReviewCreateReqDto;
import com.mongsom.dev.dto.review.reqDto.ReviewUpdateReqDto;
import com.mongsom.dev.dto.review.respDto.MyReviewRespDto;
import com.mongsom.dev.dto.review.respDto.WrittenReviewRespDto;
import com.mongsom.dev.entity.ChangeItem;
import com.mongsom.dev.entity.OrderDetail;
import com.mongsom.dev.entity.OrderItem;
import com.mongsom.dev.entity.Payments;
import com.mongsom.dev.entity.ReviewImg;
import com.mongsom.dev.entity.UserReview;
import com.mongsom.dev.repository.ChangeItemRepository;
import com.mongsom.dev.repository.OrderDetailRepository;
import com.mongsom.dev.repository.OrderItemRepository;
import com.mongsom.dev.repository.PaymentsRepository;
import com.mongsom.dev.repository.ProductImgRepository;
import com.mongsom.dev.repository.ProductOptionRepository;
import com.mongsom.dev.repository.ProductRepository;
import com.mongsom.dev.repository.ReviewImgRepository;
import com.mongsom.dev.repository.UserReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyService {
    
    private final OrderItemRepository orderItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductImgRepository productImgRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductRepository productRepository;
    private final PaymentsRepository paymentsRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final UserReviewRepository userReviewRepository;
    private final ChangeItemRepository changeItemRepository;
    
    // 배송 현황 개수 조회 (3개월 이내)
    public RespDto<DeliveryStatusRespDto> getDeliveryStatusCount(Long userCode) {
        try {
            Integer paymentCompleted = 0;
            Integer preparing = 0;
            Integer shipping = 0;
            Integer delivered = 0;
            
            try {
                paymentCompleted = orderItemRepository.countByUserCodeAndPaymentAtAfterAndDeliveryStatus(
                        userCode, "결제완료");
            } catch (Exception e) {
                log.error("결제완료 개수 조회 실패: {}", e.getMessage());
            }
            
            try {
                preparing = orderItemRepository.countByUserCodeAndPaymentAtAfterAndDeliveryStatus(
                        userCode, "상품준비중");
            } catch (Exception e) {
                log.error("상품준비중 개수 조회 실패: {}", e.getMessage());
            }
            
            try {
                shipping = orderItemRepository.countByUserCodeAndPaymentAtAfterAndDeliveryStatus(
                        userCode, "배송중");
            } catch (Exception e) {
                log.error("배송중 개수 조회 실패: {}", e.getMessage());
            }
            
            try {
                delivered = orderItemRepository.countByUserCodeAndPaymentAtAfterAndDeliveryStatus(
                        userCode, "배송완료");
            } catch (Exception e) {
                log.error("배송완료 개수 조회 실패: {}", e.getMessage());
            }
            
            DeliveryStatusRespDto respDto = DeliveryStatusRespDto.builder()
                    .paymentCompleted(paymentCompleted)
                    .preparing(preparing)
                    .shipping(shipping)
                    .delivered(delivered)
                    .build();
            
            log.info("배송 현황 조회 완료 - userCode: {}, 결제완료: {}, 상품준비중: {}, 배송중: {}, 배송완료: {}", 
                    userCode, paymentCompleted, preparing, shipping, delivered);
            
            return RespDto.<DeliveryStatusRespDto>builder()
                    .code(1)
                    .data(respDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("배송 현황 조회 실패 - userCode: {}, error: {}", userCode, e.getMessage());
            return RespDto.<DeliveryStatusRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    
    // 주문내역 조회 - Object[] 방식으로 수정
    public RespDto<List<MyOrderRespDto>> getMyOrders(Long userCode) {
        try {
            log.info("=== 주문내역 조회 시작 - userCode: {} ===", userCode);
            
            // 1. 사용자의 주문 기본 정보 조회
            List<OrderItem> orderItems = orderItemRepository.findOrdersByUserCode(userCode);
            log.info("주문 기본 정보 조회 완료 - 주문 개수: {}", orderItems.size());
            
            List<MyOrderRespDto> orderList = orderItems.stream()
                    .map(orderItem -> {
                        log.info("--- 주문 처리 시작 - orderId: {} ---", orderItem.getOrderId());
                        
                        // 2. 주문 상세 정보 조회 (Object[] 방식으로)
                        List<Object[]> orderDetailRows = orderDetailRepository.findOrderDetailsByOrderId(orderItem.getOrderId());
                        log.info("주문 상세 조회 완료 - orderId: {}, 상세 개수: {}", orderItem.getOrderId(), orderDetailRows.size());
                        
                        // 3. Object[]를 DTO로 변환
                        List<MyOrderDetailDto> detailList = orderDetailRows.stream()
                                .map(row -> {
                                    // Object[] 데이터 추출
                                    Integer orderDetailId = (Integer) row[0];
                                    Integer orderId = (Integer) row[1];
                                    Integer userCodeFromDetail = (Integer) row[2];
                                    Integer optId = (Integer) row[3];
                                    Integer productId = (Integer) row[4];
                                    Integer quantity = (Integer) row[5];
                                    
                                    log.info("OrderDetail 데이터 - detailId: {}, productId: {}, optId: {}", 
                                            orderDetailId, productId, optId);
                                    
                                    // 상품명 조회
                                    String productName = "상품명 조회 실패";
                                    try {
                                        productName = productRepository.findById(productId)
                                                .map(product -> product.getName())
                                                .orElse("상품 없음");
                                        log.info("상품명 조회 완료 - productId: {}, name: {}", productId, productName);
                                    } catch (Exception e) {
                                        log.error("상품명 조회 실패 - productId: {}, error: {}", productId, e.getMessage());
                                    }
                                    
                                    // 옵션명 조회
                                    String optName = null;
                                    if (optId != null && optId > 0) {
                                        try {
                                            optName = productOptionRepository.findOptNameByOptId(optId);
                                            log.info("옵션명 조회 완료 - optId: {}, optName: {}", optId, optName);
                                        } catch (Exception e) {
                                            log.error("옵션명 조회 실패 - optId: {}, error: {}", optId, e.getMessage());
                                        }
                                    }
                                    
                                    // 상품 이미지 조회
                                    List<String> productImgUrls = List.of();
                                    try {
                                        productImgUrls = productImgRepository.findImgUrlsByProductId(productId);
                                        if (productImgUrls == null) {
                                            productImgUrls = List.of();
                                        }
                                        log.info("이미지 조회 완료 - productId: {}, 이미지 개수: {}", productId, productImgUrls.size());
                                    } catch (Exception e) {
                                        log.error("이미지 조회 실패 - productId: {}, error: {}", productId, e.getMessage());
                                    }
                                    
                                    MyOrderDetailDto detailDto = MyOrderDetailDto.builder()
                                            .productId(productId)
                                            .productName(productName)
                                            .optId(optId)
                                            .optName(optName)
                                            .productImgUrls(productImgUrls)
                                            .build();
                                    
                                    log.info("Detail DTO 생성 완료 - productName: {}, optName: {}", 
                                            productName, optName);
                                    
                                    return detailDto;
                                })
                                .collect(Collectors.toList());
                        
                        log.info("주문 상세 변환 완료 - orderId: {}, details 개수: {}", orderItem.getOrderId(), detailList.size());
                        
                        // 4. 최종 주문 DTO 생성
                        MyOrderRespDto orderDto = MyOrderRespDto.builder()
                                .orderId(orderItem.getOrderId())
                                .paymentAt(orderItem.getPaymentAt())
                                .deliveryStatus(orderItem.getDeliveryStatus())
                                .finalPrice(orderItem.getFinalPrice())
                                .details(detailList)
                                .build();
                        
                        log.info("주문 DTO 생성 완료 - orderId: {}, details 개수: {}", 
                                orderItem.getOrderId(), orderDto.getDetails().size());
                        log.info("--- 주문 처리 완료 - orderId: {} ---", orderItem.getOrderId());
                        
                        return orderDto;
                    })
                    .collect(Collectors.toList());
            
            log.info("=== 주문내역 조회 완료 - userCode: {}, 전체 주문 개수: {} ===", userCode, orderList.size());
            
            return RespDto.<List<MyOrderRespDto>>builder()
                    .code(1)
                    .data(orderList)
                    .build();
                    
        } catch (Exception e) {
            log.error("주문내역 조회 실패 - userCode: {}, error: {}", userCode, e.getMessage(), e);
            return RespDto.<List<MyOrderRespDto>>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }

    // 주문상세 조회
    public RespDto<MyOrderDetailRespDto> getMyOrderDetail(Integer orderId) {
        try {
            // 1. 주문 기본 정보 조회 (OrderItem 직접 조회)
            OrderItem orderItem = orderItemRepository.findById(orderId).orElse(null);
            if (orderItem == null) {
                return RespDto.<MyOrderDetailRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            // 2. 결제 정보 조회
            Optional<Payments> paymentInfo = paymentsRepository.findPaymentInfoByOrderId(orderId);
            
            // 3. 주문 상세 상품 정보 조회
            List<Object[]> orderDetailRows = orderDetailRepository.findOrderDetailItemsByOrderId(orderId);
            
            // 4. 데이터 변환
            MyOrderDetailRespDto respDto = buildOrderDetailResponse(orderItem, paymentInfo, orderDetailRows);
            
            return RespDto.<MyOrderDetailRespDto>builder()
                    .code(1)
                    .data(respDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("주문상세 조회 실패 - orderId: {}, error: {}", orderId, e.getMessage());
            return RespDto.<MyOrderDetailRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }

    // 주문상세 응답 DTO 구성 - OrderItem 객체 사용
    private MyOrderDetailRespDto buildOrderDetailResponse(OrderItem orderItem, Optional<Payments> paymentInfo, List<Object[]> orderDetailRows) {
//    	System.out.println(Arrays.deepToString(paymentInfo));
    	System.out.println(paymentInfo);
    	Payments payments = paymentInfo.get();
//        // 결제 정보 추출
//        String paymentMethod = paymentInfo != null ? (String) paymentInfo[0] : null;
//        Integer paymentAmount = paymentInfo != null ? (Integer) paymentInfo[1] : null;
//        String paymentStatus = paymentInfo != null ? (String) paymentInfo[2] : null;
//        String pgProvider = paymentInfo != null ? (String) paymentInfo[3] : null;
//        System.out.println(paymentMethod);
        // 주문 상세 상품 정보 변환
        List<MyOrderDetailItemDto> detailItemList = orderDetailRows.stream()
                .map(this::convertToOrderDetailItemDto)
                .collect(Collectors.toList());
        
        return MyOrderDetailRespDto.builder()
                .orderId(orderItem.getOrderId())
                .paymentAt(orderItem.getPaymentAt())
                .deliveryStatus(orderItem.getDeliveryStatus())
                .finalPrice(orderItem.getFinalPrice())
                .userCode(orderItem.getUserCode().longValue())
                .receivedUserName(orderItem.getReceivedUserName())
                .receivedUserPhone(orderItem.getReceivedUserPhone())
                .receivedUserZipCode(orderItem.getReceivedUserZipCode())
                .receivedUserAddress(orderItem.getReceivedUserAddress())
                .receivedUserAddress2(orderItem.getReceivedUserAddress2())
                .message(orderItem.getMessage())
                .changeState(orderItem.getChangeState())
                .paymentMethod(payments.getPaymentMethod())
                .paymentAmount(payments.getPaymentAmount())
                .paymentStatus(payments.getPaymentStatus())
                .pgProvider(payments.getPgProvider())
                .details(detailItemList)
                .build();
    }
    
    // Object[]을 MyOrderDetailItemDto로 변환
    private MyOrderDetailItemDto convertToOrderDetailItemDto(Object[] row) {
    	Integer orderDetailId = (Integer) row[0];
        Integer productId = (Integer) row[4];
        Integer optId = (Integer) row[3];
        Integer quantity = (Integer) row[5];
        Integer price = (Integer) row[6];
        Integer orderStatus = (Integer) row[7];
        Integer changeStatus = row[8] != null ? (Integer) row[8] : null;
        
        String productName = getProductName(productId);
        String optName = getOptName(optId);
        List<String> productImgUrls = getProductImages(productId);
        
        return MyOrderDetailItemDto.builder()
        		.orderDetailId(orderDetailId)
                .productId(productId)
                .productName(productName)
                .optId(optId)
                .optName(optName)
                .changeStatus(changeStatus)
                .productImgUrls(productImgUrls)
                .quantity(quantity)
                .price(price)
                .orderStatus(orderStatus)
                .build();
    }
    
    // 상품명 조회
    private String getProductName(Integer productId) {
        try {
            return productRepository.findById(productId)
                    .map(product -> product.getName())
                    .orElse("상품명 없음");
        } catch (Exception e) {
            log.error("상품명 조회 실패 - productId: {}", productId);
            return "상품명 조회 실패";
        }
    }
    
    // 옵션명 조회
    private String getOptName(Integer optId) {
        if (optId == null || optId <= 0) {
            return null;
        }
        try {
            return productOptionRepository.findOptNameByOptId(optId);
        } catch (Exception e) {
            log.error("옵션명 조회 실패 - optId: {}", optId);
            return null;
        }
    }
    
    // 상품 이미지 조회
    private List<String> getProductImages(Integer productId) {
        try {
            List<String> images = productImgRepository.findImgUrlsByProductId(productId);
            return images != null ? images : List.of();
        } catch (Exception e) {
            log.error("상품 이미지 조회 실패 - productId: {}", productId);
            return List.of();
        }
    }
    
    // 리뷰 작성 가능 상품 조회
	public RespDto<MyReviewRespDto> getReviewableProducts(Long userCode, Integer page, Integer size) {
	    try {
	        log.info("=== 리뷰 작성 가능 상품 조회 시작 - userCode: {}, page: {}, size: {} ===", userCode, page, size);
	        
	        // 페이지 번호 검증 (1-based를 0-based로 변환)
	        if (page < 1) page = 1;
	        if (size < 1) size = 10;
	        
	        int offset = (page - 1) * size;
	        
	        // 1. 총 개수 조회
	        Integer totalCount = orderDetailRepository.countReviewableProducts(userCode);
	        log.info("리뷰 작성 가능 상품 총 개수: {}", totalCount);
	        
	        // 2. 페이징 데이터 조회
	        List<Object[]> reviewableRows = orderDetailRepository.findReviewableProducts(userCode, size, offset);
	        log.info("리뷰 작성 가능 상품 조회 완료 - 조회된 개수: {}", reviewableRows.size());
	        
	        // 3. Object[]를 DTO로 변환
	        List<MyReviewRespDto.MyReviewItemDto> itemList = reviewableRows.stream()
	                .map(row -> {
	                    // Object[] 데이터 추출
	                    Integer orderDetailId = (Integer) row[0];
	                    Integer optId = (Integer) row[1];
	                    Integer productId = (Integer) row[2];
	                    Integer reviewStatus = (Integer) row[3];
	                    String optName = (String) row[4];
	                    String productName = (String) row[5];
	                    java.sql.Timestamp paymentAtTimestamp = (java.sql.Timestamp) row[6];
	                    LocalDateTime paymentAt = paymentAtTimestamp != null ? paymentAtTimestamp.toLocalDateTime() : null;
	                    
	                    log.info("리뷰 상품 데이터 - orderDetailId: {}, productId: {}, optId: {}, productName: {}", 
	                            orderDetailId, productId, optId, productName);
	                    
	                    // 상품 이미지 조회
	                    List<String> productImgUrls = getProductImages(productId);
	                    
	                    return MyReviewRespDto.MyReviewItemDto.builder()
	                            .orderDetailId(orderDetailId)
	                            .optId(optId)
	                            .productId(productId)
	                            .reviewStatus(reviewStatus)
	                            .optName(optName)
	                            .productName(productName)
	                            .productImgUrls(productImgUrls)
	                            .paymentAt(paymentAt)
	                            .build();
	                })
	                .collect(Collectors.toList());
	        
	        // 4. 페이지네이션 정보 생성
	        int totalPages = (int) Math.ceil((double) totalCount / size);
	        boolean hasNext = page < totalPages;
	        
	        MyReviewRespDto.PaginationDto pagination = MyReviewRespDto.PaginationDto.builder()
	                .currentPage(page)
	                .totalPage(totalPages)
	                .size(size)
	                .hasNext(hasNext)
	                .build();
	        
	        // 5. 최종 응답 DTO 생성
	        MyReviewRespDto respDto = MyReviewRespDto.builder()
	                .items(itemList)
	                .pagination(pagination)
	                .build();
	        
	        log.info("=== 리뷰 작성 가능 상품 조회 완료 - userCode: {}, 상품 개수: {}, 총 페이지: {} ===", 
	                userCode, itemList.size(), totalPages);
	        
	        return RespDto.<MyReviewRespDto>builder()
	                .code(1)
	                .data(respDto)
	                .build();
	                
	    } catch (Exception e) {
	        log.error("리뷰 작성 가능 상품 조회 실패 - userCode: {}, error: {}", userCode, e.getMessage());
	        return RespDto.<MyReviewRespDto>builder()
	                .code(-1)
	                .data(null)
	                .build();
	    }
	}
	
	/**
	 * 작성한 리뷰 조회
	 */
	public RespDto<WrittenReviewRespDto> getWrittenReviews(Long userCode, Integer page, Integer size) {
	    try {
	        log.info("=== 작성한 리뷰 조회 시작 - userCode: {}, page: {}, size: {} ===", userCode, page, size);
	        
	        // 1. 오프셋 계산
	        int offset = (page - 1) * size;
	        
	        // 2. 총 개수 조회
	        Integer totalCount = orderDetailRepository.countWrittenReviews(userCode);
	        log.info("작성한 리뷰 총 개수: {}", totalCount);
	        
	        // 3. 페이징 데이터 조회
	        List<Object[]> writtenReviewRows = orderDetailRepository.findWrittenReviews(userCode, size, offset);
	        log.info("작성한 리뷰 조회 완료 - 조회된 개수: {}", writtenReviewRows.size());
	        
	        // 4. Object[]를 DTO로 변환
	        List<WrittenReviewRespDto.WrittenReviewItemDto> itemList = writtenReviewRows.stream()
	                .map(row -> {
	                    // Object[] 데이터 추출
	                    Integer orderDetailId = (Integer) row[0];
	                    Integer optId = (Integer) row[1];
	                    Integer productId = (Integer) row[2];
	                    
	                    // review_status 안전한 캐스팅
	                    Object reviewStatusObj = row[3];
	                    Integer reviewStatus;
	                    if (reviewStatusObj instanceof Boolean) {
	                        reviewStatus = ((Boolean) reviewStatusObj) ? 1 : 0;
	                    } else if (reviewStatusObj instanceof Integer) {
	                        reviewStatus = (Integer) reviewStatusObj;
	                    } else {
	                        reviewStatus = 0;
	                    }
	                    
	                    String optName = (String) row[4];
	                    String productName = (String) row[5];
	                    Integer reviewId = (Integer) row[6];
	                    Integer reviewRating = (Integer) row[7];
	                    String reviewContent = (String) row[8];
	                    java.sql.Timestamp reviewCreatedAtTimestamp = (java.sql.Timestamp) row[9];
	                    LocalDateTime reviewCreatedAt = reviewCreatedAtTimestamp != null ? reviewCreatedAtTimestamp.toLocalDateTime() : null;
	                    
	                    log.info("작성한 리뷰 데이터 - orderDetailId: {}, reviewId: {}, productId: {}, productName: {}", 
	                            orderDetailId, reviewId, productId, productName);
	                    
	                    // 상품 이미지 조회
	                    List<String> productImgUrls = getProductImages(productId);
	                    
	                    // 리뷰 이미지 조회
	                    List<String> reviewImgUrls = getReviewImages(reviewId);
	                    
	                    return WrittenReviewRespDto.WrittenReviewItemDto.builder()
	                            .orderDetailId(orderDetailId)
	                            .optId(optId)
	                            .productId(productId)
	                            .reviewStatus(reviewStatus)
	                            .optName(optName)
	                            .productName(productName)
	                            .productImgUrls(productImgUrls)
	                            .reviewId(reviewId)
	                            .reviewRating(reviewRating)
	                            .reviewContent(reviewContent)
	                            .reviewCreatedAt(reviewCreatedAt)
	                            .reviewImgUrls(reviewImgUrls)
	                            .build();
	                })
	                .collect(Collectors.toList());
	        
	        // 5. 페이지네이션 정보 생성
	        int totalPages = (int) Math.ceil((double) totalCount / size);
	        boolean hasNext = page < totalPages;
	        
	        WrittenReviewRespDto.PaginationDto pagination = WrittenReviewRespDto.PaginationDto.builder()
	                .currentPage(page)
	                .totalPage(totalPages)
	                .size(size)
	                .hasNext(hasNext)
	                .build();
	        
	        // 6. 최종 응답 DTO 생성
	        WrittenReviewRespDto respDto = WrittenReviewRespDto.builder()
	                .items(itemList)
	                .pagination(pagination)
	                .build();
	        
	        log.info("=== 작성한 리뷰 조회 완료 - userCode: {}, 리뷰 개수: {}, 총 페이지: {} ===", 
	                userCode, itemList.size(), totalPages);
	        
	        return RespDto.<WrittenReviewRespDto>builder()
	                .code(1)
	                .data(respDto)
	                .build();
	                
	    } catch (Exception e) {
	        log.error("작성한 리뷰 조회 실패 - userCode: {}, error: {}", userCode, e.getMessage());
	        return RespDto.<WrittenReviewRespDto>builder()
	                .code(-1)
	                .data(null)
	                .build();
	    }
	}

	/**
	 * 리뷰 이미지 조회 헬퍼 메서드
	 */
	private List<String> getReviewImages(Integer reviewId) {
	    try {
	        // review_img 테이블에서 review_id로 이미지 URL 조회
	        List<Object[]> reviewImgRows = reviewImgRepository.findByReviewId(reviewId);
	        return reviewImgRows.stream()
	                .map(row -> (String) row[0]) // review_img_url
	                .collect(Collectors.toList());
	    } catch (Exception e) {
	        log.warn("리뷰 이미지 조회 실패 - reviewId: {}, error: {}", reviewId, e.getMessage());
	        return new ArrayList<>();
	    }
	}
	
	// 리뷰 작성
	@Transactional
	public RespDto<String> createReview(ReviewCreateReqDto reqDto) {
	    try {
	        log.info("=== 리뷰 작성 시작 - orderItemId: {}, userCode: {} ===", 
	                reqDto.getOrderDetailId(), reqDto.getUserCode());
	        
	        // 1. order_detail 조회 및 검증
	        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(reqDto.getOrderDetailId());
	        if (orderDetailOpt.isEmpty()) {
	            log.error("주문 상세 정보를 찾을 수 없습니다 - orderDetailId: {}", reqDto.getOrderDetailId());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("주문 상세 정보를 찾을 수 없습니다.")
	                    .build();
	        }
	        
	        OrderDetail orderDetail = orderDetailOpt.get();
	        
	        // 2. 사용자 검증
	        if (!orderDetail.getUserCode().equals(reqDto.getUserCode())) {
	            log.error("사용자 정보가 일치하지 않습니다 - orderDetail userCode: {}, request userCode: {}", 
	                    orderDetail.getUserCode(), reqDto.getUserCode());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("사용자 정보가 일치하지 않습니다.")
	                    .build();
	        }
	        
	        // 3. 이미 리뷰가 작성되었는지 확인
	        if (orderDetail.getReviewStatus() != null && orderDetail.getReviewStatus() == 1) {
	            log.error("이미 리뷰가 작성된 주문입니다 - orderDetailId: {}", reqDto.getOrderDetailId());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("이미 리뷰가 작성된 주문입니다.")
	                    .build();
	        }
	        
	        // 4. UserReview 엔티티 생성 및 저장
	        UserReview userReview = UserReview.builder()
	                .userCode(reqDto.getUserCode())
	                .productId(orderDetail.getProductId())
	                .orderDetailId(reqDto.getOrderDetailId())
	                .orderId(orderDetail.getOrderId()) // ⭐ 추가
	                .reviewRating(reqDto.getReviewRating())
	                .reviewContent(reqDto.getReviewContent())
	                .createdAt(LocalDateTime.now())
	                .updatedAt(LocalDateTime.now())
	                .build();
	        
	        UserReview savedReview = userReviewRepository.save(userReview);
	        log.info("UserReview 저장 완료 - reviewId: {}", savedReview.getReviewId());
	        
	        // 5. ReviewImg 저장 (이미지가 있는 경우)
	        if (reqDto.getReviewImgUrls() != null && !reqDto.getReviewImgUrls().isEmpty()) {
	            List<ReviewImg> reviewImgs = new ArrayList<>();
	            for (String imgUrl : reqDto.getReviewImgUrls()) {
	                ReviewImg reviewImg = ReviewImg.builder()
	                        .reviewId(savedReview.getReviewId())
	                        .reviewImgUrl(imgUrl)
	                        .createdAt(LocalDateTime.now())
	                        .updatedAt(LocalDateTime.now())
	                        .build();
	                reviewImgs.add(reviewImg);
	            }
	            
	            reviewImgRepository.saveAll(reviewImgs);
	            log.info("ReviewImg 저장 완료 - 이미지 개수: {}", reviewImgs.size());
	        }
	        
	        // 6. OrderDetail의 review_status를 1로 업데이트
	        int updatedRows = orderDetailRepository.updateReviewStatus(reqDto.getOrderDetailId());
	        if (updatedRows > 0) {
	            log.info("OrderDetail review_status 업데이트 완료 - orderDetailId: {}", reqDto.getOrderDetailId());
	        } else {
	            log.warn("OrderDetail review_status 업데이트 실패 - orderDetailId: {}", reqDto.getOrderDetailId());
	        }
	        
	        log.info("=== 리뷰 작성 완료 - reviewId: {}, orderDetailId: {} ===", 
	                savedReview.getReviewId(), reqDto.getOrderDetailId());
	        
	        return RespDto.<String>builder()
	                .code(1)
	                .data("리뷰가 성공적으로 작성되었습니다.")
	                .build();
	                
	    } catch (Exception e) {
	        return RespDto.<String>builder()
	                .code(-1)
	                .data("리뷰 작성 중 오류가 발생했습니다.")
	                .build();
	    }
	}
	
	// 리뷰 수정
	@Transactional
	public RespDto<String> updateReview(ReviewUpdateReqDto reqDto) {
	    try {
	        log.info("=== 리뷰 수정 시작 - reviewId: {}, userCode: {} ===", 
	                reqDto.getReviewId(), reqDto.getUserCode());
	        
	        // 1. 기존 리뷰 조회 및 검증
	        Optional<UserReview> userReviewOpt = userReviewRepository.findById(reqDto.getReviewId());
	        if (userReviewOpt.isEmpty()) {
	            log.error("리뷰를 찾을 수 없습니다 - reviewId: {}", reqDto.getReviewId());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("리뷰를 찾을 수 없습니다.")
	                    .build();
	        }
	        
	        UserReview existingReview = userReviewOpt.get();
	        
	        // 2. 작성자 검증
	        if (!existingReview.getUserCode().equals(reqDto.getUserCode())) {
	            log.error("리뷰 작성자가 아닙니다 - reviewId: {}, requestUserCode: {}, reviewUserCode: {}", 
	                    reqDto.getReviewId(), reqDto.getUserCode(), existingReview.getUserCode());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("리뷰 수정 권한이 없습니다.")
	                    .build();
	        }
	        
	        // 3. 리뷰 정보 업데이트
	        existingReview.setReviewRating(reqDto.getReviewRating());
	        existingReview.setReviewContent(reqDto.getReviewContent());
	        existingReview.setUpdatedAt(LocalDateTime.now());
	        
	        UserReview updatedReview = userReviewRepository.save(existingReview);
	        log.info("UserReview 업데이트 완료 - reviewId: {}", updatedReview.getReviewId());
	        
	        // 4. 기존 리뷰 이미지 삭제
	        reviewImgRepository.deleteByReviewId(reqDto.getReviewId());
	        log.info("기존 리뷰 이미지 삭제 완료 - reviewId: {}", reqDto.getReviewId());
	        
	        // 5. 새로운 리뷰 이미지 저장 (이미지가 있는 경우)
	        if (reqDto.getReviewImgUrls() != null && !reqDto.getReviewImgUrls().isEmpty()) {
	            List<ReviewImg> newReviewImgs = new ArrayList<>();
	            for (String imgUrl : reqDto.getReviewImgUrls()) {
	                ReviewImg reviewImg = ReviewImg.builder()
	                        .reviewId(reqDto.getReviewId())
	                        .reviewImgUrl(imgUrl)
	                        .createdAt(LocalDateTime.now())
	                        .updatedAt(LocalDateTime.now())
	                        .build();
	                newReviewImgs.add(reviewImg);
	            }
	            
	            reviewImgRepository.saveAll(newReviewImgs);
	            log.info("새 리뷰 이미지 저장 완료 - reviewId: {}, 이미지 개수: {}", 
	                    reqDto.getReviewId(), newReviewImgs.size());
	        }
	        
	        log.info("=== 리뷰 수정 완료 - reviewId: {} ===", reqDto.getReviewId());
	        
	        return RespDto.<String>builder()
	                .code(1)
	                .data("리뷰가 성공적으로 수정되었습니다.")
	                .build();
	                
	    } catch (Exception e) {
	        log.error("리뷰 수정 실패 - reviewId: {}, userCode: {}, error: {}", 
	                reqDto.getReviewId(), reqDto.getUserCode(), e.getMessage());
	        return RespDto.<String>builder()
	                .code(-1)
	                .data("리뷰 수정 중 오류가 발생했습니다.")
	                .build();
	    }
	}
	
	// 리뷰 삭제
	@Transactional
	public RespDto<Boolean> deleteReview(Integer reviewId, Long userCode) {
	    try {
	        log.info("=== 리뷰 삭제 시작 - reviewId: {}, userCode: {} ===", reviewId, userCode);
	        
	        // 1. 기존 리뷰 조회 및 검증
	        Optional<UserReview> userReviewOpt = userReviewRepository.findById(reviewId);
	        if (userReviewOpt.isEmpty()) {
	            log.error("리뷰를 찾을 수 없습니다 - reviewId: {}", reviewId);
	            return RespDto.<Boolean>builder()
	                    .code(-1)
	                    .data(false)
	                    .build();
	        }
	        
	        UserReview existingReview = userReviewOpt.get();
	        
	        // 2. 작성자 검증
	        if (!existingReview.getUserCode().equals(userCode)) {
	            log.error("리뷰 작성자가 아닙니다 - reviewId: {}, requestUserCode: {}, reviewUserCode: {}", 
	                    reviewId, userCode, existingReview.getUserCode());
	            return RespDto.<Boolean>builder()
	                    .code(-1)
	                    .data(false)
	                    .build();
	        }
	        
	        // 3. 리뷰 이미지 삭제 (외래키 관계로 인해 먼저 삭제)
	        reviewImgRepository.deleteByReviewId(reviewId);
	        log.info("리뷰 이미지 삭제 완료 - reviewId: {}", reviewId);
	        
	        // 4. 사용자 리뷰 삭제
	        userReviewRepository.deleteById(reviewId);
	        log.info("사용자 리뷰 삭제 완료 - reviewId: {}", reviewId);
	        
	        // 5. order_detail의 review_status를 0으로 변경 (리뷰 미작성 상태로)
	        orderDetailRepository.updateReviewStatusToZero(existingReview.getOrderDetailId());
	        log.info("OrderDetail review_status를 0으로 변경 완료 - orderDetailId: {}", existingReview.getOrderDetailId());
	        
	        log.info("=== 리뷰 삭제 완료 - reviewId: {} ===", reviewId);
	        
	        return RespDto.<Boolean>builder()
	                .code(1)
	                .data(true)
	                .build();
	                
	    } catch (Exception e) {
	        log.error("리뷰 삭제 실패 - reviewId: {}, userCode: {}, error: {}", 
	                reviewId, userCode, e.getMessage());
	        return RespDto.<Boolean>builder()
	                .code(-1)
	                .data(false)
	                .build();
	    }
	}
	
	// 배송 정보 조회
	public RespDto<DeliveryRespDto> getDeliveryInfo(Integer orderId) {
	    try {
	        log.info("=== 배송 정보 조회 시작 - orderId: {} ===", orderId);
	        
	        // 1. 주문 ID로 배송 정보 조회
	        List<Object[]> deliveryInfoList = orderItemRepository.findDeliveryInfoByOrderId(orderId);
	        
	        if (deliveryInfoList.isEmpty()) {
	            log.error("주문 정보를 찾을 수 없습니다 - orderId: {}", orderId);
	            return RespDto.<DeliveryRespDto>builder()
	                    .code(-1)
	                    .data(null)
	                    .build();
	        }
	        
	        // 2. 첫 번째 결과 가져오기
	        Object[] deliveryInfo = deliveryInfoList.get(0);
	        String deliveryCom = null;
	        String invoiceNum = null;
	        
	        // 3. 안전한 캐스팅
	        if (deliveryInfo[0] != null) {
	            deliveryCom = deliveryInfo[0].toString();
	        }
	        if (deliveryInfo[1] != null) {
	            invoiceNum = deliveryInfo[1].toString();
	        }
	        
	        // 4. 배송 정보가 없는 경우 (택배회사나 송장번호가 null)
	        if (deliveryCom == null && invoiceNum == null) {
	            log.warn("배송 정보가 등록되지 않았습니다 - orderId: {}", orderId);
	            return RespDto.<DeliveryRespDto>builder()
	                    .code(-1)
	                    .data(null)
	                    .build();
	        }
	        
	        // 5. 응답 DTO 생성
	        DeliveryRespDto respDto = DeliveryRespDto.builder()
	                .deliveryCom(deliveryCom)
	                .invoiceNum(invoiceNum)
	                .build();
	        
	        log.info("배송 정보 조회 완료 - orderId: {}, deliveryCom: {}, invoiceNum: {}", 
	                orderId, deliveryCom, invoiceNum);
	        
	        return RespDto.<DeliveryRespDto>builder()
	                .code(1)
	                .data(respDto)
	                .build();
	                
	    } catch (Exception e) {
	        log.error("배송 정보 조회 실패 - orderId: {}, error: {}", orderId, e.getMessage());
	        return RespDto.<DeliveryRespDto>builder()
	                .code(-1)
	                .data(null)
	                .build();
	    }
	}
	
	//교환/반품 신청
	@Transactional
	public RespDto<String> createChangeRequest(ChangeCreateReqDto reqDto) {
	    try {
	        log.info("=== 교환/반품 신청 시작 - orderItemId: {}, orderId: {}, userCode: {}, changeStatus: {} ===", 
	                reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getChangeStatus());
	        
	        // 1. 주문 상품 존재 여부 확인 (order_detail 테이블에서)
	        Optional<OrderDetail> orderDetailOpt = orderDetailRepository.findById(reqDto.getOrderDetailId());
	        if (orderDetailOpt.isEmpty()) {
	            log.error("주문 상품을 찾을 수 없습니다 - orderItemId: {}", reqDto.getOrderDetailId());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("주문 상품을 찾을 수 없습니다.")
	                    .build();
	        }
	        
	        OrderDetail orderDetail = orderDetailOpt.get();
	        
	        // 2. 사용자 권한 확인
	        if (!orderDetail.getUserCode().equals(reqDto.getUserCode())) {
	            log.error("주문 상품의 소유자가 아닙니다 - orderDetail userCode: {}, request userCode: {}", 
	                    orderDetail.getUserCode(), reqDto.getUserCode());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("해당 주문 상품에 대한 권한이 없습니다.")
	                    .build();
	        }
	        
	        // 3. 주문 ID 일치 확인
	        if (!orderDetail.getOrderId().equals(reqDto.getOrderId())) {
	            log.error("주문 ID가 일치하지 않습니다 - orderDetail orderId: {}, request orderId: {}", 
	                    orderDetail.getOrderId(), reqDto.getOrderId());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("주문 정보가 일치하지 않습니다.")
	                    .build();
	        }
	        
	        // 4. ChangeItem 엔티티 생성 및 저장
	        ChangeItem changeItem = ChangeItem.builder()
	                .orderDetailId(reqDto.getOrderDetailId())
	                .orderId(reqDto.getOrderId())
	                .userCode(reqDto.getUserCode())
	                .changeStatus(reqDto.getChangeStatus())
	                .contents(reqDto.getContents())
	                .approvalStatus(ChangeItem.ApprovalStatus.PENDING) // 기본값: 대기(0)
	                .build();
	        
	        ChangeItem savedChangeItem = changeItemRepository.save(changeItem);
	        
	        // 5. 신청 타입 문자열 생성
	        String changeTypeStr = (reqDto.getChangeStatus() == ChangeItem.ChangeStatus.EXCHANGE) ? "교환" : "반품";
	        
	        log.info("=== {}신청 완료 - changeId: {}, orderItemId: {} ===", 
	                changeTypeStr, savedChangeItem.getChangeId(), reqDto.getOrderDetailId());
	        
	        return RespDto.<String>builder()
	                .code(1)
	                .data(changeTypeStr + " 신청이 성공적으로 접수되었습니다.")
	                .build();
	                
	    } catch (Exception e) {
	        log.error("교환/반품 신청 실패 - orderItemId: {}, userCode: {}, error: {}", 
	                reqDto.getOrderDetailId(), reqDto.getUserCode(), e.getMessage());
	        return RespDto.<String>builder()
	                .code(-1)
	                .data("교환/반품 신청 중 오류가 발생했습니다.")
	                .build();
	    }
	}
	
	/**
	 * 교환/반품 신청 취소
	 */
	@Transactional
	public RespDto<String> deleteChangeRequest(ChangeDeleteReqDto reqDto) {
	    try {
	        log.info("=== 교환/반품 신청 취소 시작 - orderItemId: {}, orderId: {}, userCode: {} ===", 
	                reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	        
	        // 1. 삭제할 교환/반품 신청 조회
	        List<ChangeItem> changeItems = changeItemRepository.findByOrderItemIdAndOrderIdAndUserCode(
	                reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	        
	        if (changeItems.isEmpty()) {
	            log.error("교환/반품 신청 내역을 찾을 수 없습니다 - orderItemId: {}, orderId: {}, userCode: {}", 
	                    reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("교환/반품 신청 내역을 찾을 수 없습니다.")
	                    .build();
	        }
	        
	        // 2. 승인 상태 확인 (승인된 건은 취소 불가)
	        for (ChangeItem changeItem : changeItems) {
	            if (changeItem.getApprovalStatus() == ChangeItem.ApprovalStatus.APPROVED) {
	                log.error("이미 승인된 교환/반품 신청은 취소할 수 없습니다 - changeId: {}", changeItem.getChangeId());
	                return RespDto.<String>builder()
	                        .code(-1)
	                        .data("이미 승인된 교환/반품 신청은 취소할 수 없습니다.")
	                        .build();
	            }
	        }
	        
	        // 3. 교환/반품 신청 삭제
	        int deletedCount = changeItemRepository.deleteByOrderItemIdAndOrderIdAndUserCode(
	                reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	        
	        if (deletedCount > 0) {
	            log.info("교환/반품 신청 취소 완료 - 삭제된 건수: {}, orderItemId: {}, orderId: {}, userCode: {}", 
	                    deletedCount, reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	            
	            return RespDto.<String>builder()
	                    .code(1)
	                    .data("교환/반품 신청이 성공적으로 취소되었습니다.")
	                    .build();
	        } else {
	            log.error("교환/반품 신청 삭제 실패 - orderItemId: {}, orderId: {}, userCode: {}", 
	                    reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode());
	            return RespDto.<String>builder()
	                    .code(-1)
	                    .data("교환/반품 신청 취소에 실패했습니다.")
	                    .build();
	        }
	        
	    } catch (Exception e) {
	        log.error("교환/반품 신청 취소 실패 - orderItemId: {}, orderId: {}, userCode: {}, error: {}", 
	                reqDto.getOrderDetailId(), reqDto.getOrderId(), reqDto.getUserCode(), e.getMessage());
	        return RespDto.<String>builder()
	                .code(-1)
	                .data("교환/반품 신청 취소 중 오류가 발생했습니다.")
	                .build();
	    }
	}
	
}