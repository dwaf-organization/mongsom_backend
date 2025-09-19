package com.mongsom.dev.controller;

import java.util.List;

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
import com.mongsom.dev.dto.mypage.reqDto.ChangeCreateReqDto;
import com.mongsom.dev.dto.mypage.reqDto.ChangeDeleteReqDto;
import com.mongsom.dev.dto.mypage.respDto.DeliveryRespDto;
import com.mongsom.dev.dto.mypage.respDto.DeliveryStatusRespDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderDetailRespDto;
import com.mongsom.dev.dto.mypage.respDto.MyOrderRespDto;
import com.mongsom.dev.dto.review.reqDto.ReviewCreateReqDto;
import com.mongsom.dev.dto.review.reqDto.ReviewUpdateReqDto;
import com.mongsom.dev.dto.review.respDto.MyReviewRespDto;
import com.mongsom.dev.dto.review.respDto.WrittenReviewRespDto;
import com.mongsom.dev.service.MyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/my")
@RequiredArgsConstructor
@Slf4j
public class MyController {
    
    private final MyService myService;
    
    @GetMapping("/delivery/number/{userCode}")
    public ResponseEntity<RespDto<DeliveryStatusRespDto>> getDeliveryStatusCount(
            @PathVariable("userCode") Integer userCode) {
        
        log.info("배송 현황 개수 조회 요청 - userCode: {}", userCode);
        
        RespDto<DeliveryStatusRespDto> response = myService.getDeliveryStatusCount(userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    @GetMapping("/order/{userCode}")
    public ResponseEntity<RespDto<List<MyOrderRespDto>>> getMyOrders(
            @PathVariable("userCode") Integer userCode) {
        
        log.info("주문내역 조회 요청 - userCode: {}", userCode);
        
        RespDto<List<MyOrderRespDto>> response = myService.getMyOrders(userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    @GetMapping("/order/detail/{orderId}")
    public ResponseEntity<RespDto<MyOrderDetailRespDto>> getMyOrderDetail(
            @PathVariable("orderId") Integer orderId) {
        
        log.info("주문상세 조회 요청 - orderId: {}", orderId);
        
        RespDto<MyOrderDetailRespDto> response = myService.getMyOrderDetail(orderId);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    // 작성가능한 리뷰 조회
    @GetMapping("/review/{userCode}/{page}/{size}")
    public ResponseEntity<RespDto<MyReviewRespDto>> getReviewableProducts(
            @PathVariable("userCode") Integer userCode,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        
        log.info("리뷰 작성 가능 상품 조회 요청 - userCode: {}, page: {}, size: {}", userCode, page, size);
        
        RespDto<MyReviewRespDto> response = myService.getReviewableProducts(userCode, page, size);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    // 작성한 리뷰 조회
    @GetMapping("/review/write/{userCode}/{page}/{size}")
    public ResponseEntity<RespDto<WrittenReviewRespDto>> getWrittenReviews(
            @PathVariable("userCode") Integer userCode,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        
        log.info("작성한 리뷰 조회 요청 - userCode: {}, page: {}, size: {}", userCode, page, size);
        
        RespDto<WrittenReviewRespDto> response = myService.getWrittenReviews(userCode, page, size);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    // 리뷰 작성
    @PostMapping("/review/create")
    public ResponseEntity<RespDto<String>> createReview(@Valid @RequestBody ReviewCreateReqDto reqDto) {
        
        log.info("리뷰 작성 요청 - orderItemId: {}, userCode: {}, rating: {}", 
                reqDto.getOrderDetailId(), reqDto.getUserCode(), reqDto.getReviewRating());
        
        RespDto<String> response = myService.createReview(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    
    // 리뷰 수정
    @PutMapping("/review/update")
    public ResponseEntity<RespDto<String>> updateReview(@Valid @RequestBody ReviewUpdateReqDto reqDto) {
        
        log.info("리뷰 수정 요청 - reviewId: {}, userCode: {}, rating: {}", 
                reqDto.getReviewId(), reqDto.getUserCode(), reqDto.getReviewRating());
        
        RespDto<String> response = myService.updateReview(reqDto);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    // 리뷰삭제
    @DeleteMapping("/review/delete/{reviewId}")
    public ResponseEntity<RespDto<Boolean>> deleteReview(
            @PathVariable("reviewId") Integer reviewId,
            @RequestParam("userCode") Integer userCode) {  // 쿼리 파라미터로 userCode 받기
        
        log.info("리뷰 삭제 요청 - reviewId: {}, userCode: {}", reviewId, userCode);
        
        RespDto<Boolean> response = myService.deleteReview(reviewId, userCode);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
    // 배송조회
    @GetMapping("/delivery/{orderId}")
    public ResponseEntity<RespDto<DeliveryRespDto>> getDeliveryInfo(@PathVariable("orderId") Integer orderId) {
        
        log.info("배송 정보 조회 요청 - orderId: {}", orderId);
        
        RespDto<DeliveryRespDto> response = myService.getDeliveryInfo(orderId);
        HttpStatus status = response.getCode() == 1 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
   
    //교환/반품 신청
    @PostMapping("/change/create")
    public ResponseEntity<RespDto<String>> createChangeRequest(@Valid @RequestBody ChangeCreateReqDto reqDto) {
        
        String changeType = (reqDto.getChangeStatus() == 1) ? "교환" : "반품";
        log.info("{} 신청 요청 - orderItemId: {}, orderId: {}, userCode: {}", 
                changeType, reqDto.getOrderItemId(), reqDto.getOrderId(), reqDto.getUserCode());
        
        RespDto<String> response = myService.createChangeRequest(reqDto);
        
        if (response.getCode() == 1) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // 교환/반품 신청 취소
    @PostMapping("/change/delete")
    public ResponseEntity<RespDto<String>> deleteChangeRequest(@Valid @RequestBody ChangeDeleteReqDto reqDto) {
        
        log.info("교환/반품 신청 취소 요청 - orderItemId: {}, orderId: {}, userCode: {}", 
                reqDto.getOrderItemId(), reqDto.getOrderId(), reqDto.getUserCode());
        
        RespDto<String> response = myService.deleteChangeRequest(reqDto);
        
        if (response.getCode() == 1) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}