package com.mongsom.dev.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.product.reqDto.AdminDeliveryUpdateReqDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminOrderDetailRespDto;
import com.mongsom.dev.dto.admin.product.respDto.AdminOrderListWithPagingRespDto;
import com.mongsom.dev.service.admin.AdminOrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin/order")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderController {

	private final AdminOrderService adminOrderService;
	//관리자 주문 목록조회
    @GetMapping("/list/{page}/{size}")
    public ResponseEntity<RespDto<AdminOrderListWithPagingRespDto>> getOrderList(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "orderId", required = false) String orderId) {

        log.info("관리자 주문 조회 요청 - page: {}, size: {}, startDate: {}, endDate: {}, orderId: {}",
                page, size, startDate, endDate, orderId);

        RespDto<AdminOrderListWithPagingRespDto> response =
                adminOrderService.getOrderList(page, size, startDate, endDate, orderId);

        return ResponseEntity.ok(response);
    }
    
    // 관리자 주문 상세 조회
    @GetMapping("/detail/{orderId}")
    public ResponseEntity<RespDto<AdminOrderDetailRespDto>> getOrderDetail(
            @PathVariable("orderId") Integer orderId) {

        log.info("관리자 주문 상세 조회 요청 - orderId: {}", orderId);

        RespDto<AdminOrderDetailRespDto> response = adminOrderService.getOrderDetail(orderId);

        return ResponseEntity.ok(response);
    }
    
    // 배송 정보 업데이트
    @PutMapping("/delivery/update")
    public ResponseEntity<RespDto<Boolean>> updateDeliveryInfo(
            @RequestBody AdminDeliveryUpdateReqDto reqDto) {

        log.info("배송 정보 업데이트 요청 - orderId: {}, userCode: {}, deliveryStatus: {}", 
                reqDto.getOrderId(), reqDto.getUserCode(), reqDto.getDeliveryStatus());

        RespDto<Boolean> response = adminOrderService.updateDeliveryInfo(reqDto);

        return ResponseEntity.ok(response);
    }
}
