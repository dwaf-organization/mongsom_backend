package com.mongsom.dev.repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
	// 주문번호와 사용자 코드로 주문 정보 조회
    Optional<OrderItem> findByOrderIdAndUserCode(Integer orderId, Long userCode);
    
    // orderNum으로 주문 조회 (토스페이먼츠 orderId로 조회)
    Optional<OrderItem> findByOrderNum(String orderNum);
    
    // 특정 사용자의 주문 조회 (최신순) - 위치기반으로 변경
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = ?1 ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeOrderByCreatedAtDesc(Long userCode);
    
    // 특정 주문 상태의 주문들 조회 - 위치기반으로 변경
    @Query("SELECT o FROM OrderItem o WHERE o.deliveryStatus = ?1 ORDER BY o.createdAt DESC")
    List<OrderItem> findByDeliveryStatusOrderByCreatedAtDesc(String deliveryStatus);
    
    // 사용자의 특정 상태 주문 조회 - 위치기반으로 변경
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = ?1 AND o.deliveryStatus = ?2 ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeAndDeliveryStatusOrderByCreatedAtDesc(Long userCode, String deliveryStatus);
    
    // 배송 현황 개수 조회 - 위치기반으로 변경
    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.userCode = ?1 AND o.deliveryStatus = ?2")
    Integer countByUserCodeAndPaymentAtAfterAndDeliveryStatus(Long userCode, String deliveryStatus);

    // 사용자별 주문 기본 정보 조회 - 위치기반으로 변경
    @Query("SELECT oi FROM OrderItem oi " +
            "WHERE oi.userCode = ?1 " +
            "AND oi.deliveryStatus != '결제대기' " +
            "ORDER BY oi.createdAt DESC")
     List<OrderItem> findOrdersByUserCode(Long userCode);
    
    // 주문 ID로 배송 정보 조회 (택배회사, 송장번호)
    @Query(value = "SELECT delivery_com, invoice_num FROM order_item WHERE order_id = ?1", nativeQuery = true)
    List<Object[]> findDeliveryInfoByOrderId(Integer orderId);
    
    // 주문 ID로 OrderItem 엔티티 조회
    Optional<OrderItem> findByOrderId(Integer orderId);
    
    // 날짜 범위로 주문 조회 (Native Query로 변경)
    @Query(value = "SELECT * FROM order_item WHERE DATE(payment_at) BETWEEN ?1 AND ?2 ORDER BY payment_at DESC", nativeQuery = true)
    List<OrderItem> findByPaymentAtBetween(String startDate, String endDate);
    
    // 주문번호 포함 검색 + 날짜 범위 (Native Query로 변경)
    @Query(value = "SELECT * FROM order_item WHERE CAST(order_id AS CHAR) LIKE CONCAT('%', ?1, '%') AND DATE(payment_at) BETWEEN ?2 AND ?3 ORDER BY payment_at DESC", nativeQuery = true)
    List<OrderItem> findByOrderIdContainingAndPaymentAtBetween(String orderId, String startDate, String endDate);
    
    // 날짜 범위 + 페이징 (Native Query로 수정)
    @Query(value = "SELECT * FROM order_item " +
                   "WHERE DATE(payment_at) BETWEEN ?1 AND ?2 " +
                   "ORDER BY payment_at DESC", 
           nativeQuery = true)
    Page<OrderItem> findByPaymentAtBetweenWithPaging(String startDate, String endDate, Pageable pageable);

    // 주문번호 포함 + 날짜 범위 + 페이징 (Native Query로 수정)
    @Query(value = "SELECT * FROM order_item " +
                   "WHERE CAST(order_id AS CHAR) LIKE CONCAT('%', ?1, '%') " +
                   "AND DATE(payment_at) BETWEEN ?2 AND ?3 " +
                   "ORDER BY payment_at DESC", 
           nativeQuery = true)
    Page<OrderItem> findByOrderIdContainingAndPaymentAtBetweenWithPaging(String orderId, String startDate, String endDate, Pageable pageable);
    
    /**
     * 관리자 주문 조회 - 복합 검색 조건 (간단한 NULL 처리)
     */
    @Query(value = "SELECT * FROM order_item " +
           "WHERE DATE(payment_at) BETWEEN ?1 AND ?2 " +
           "AND delivery_status != '결제대기' " +
           "AND (?3 IS NULL OR CAST(order_id AS CHAR) LIKE CONCAT('%', ?3, '%')) " +
           "AND (?4 IS NULL OR received_user_name LIKE CONCAT('%', ?4, '%')) " +
           "AND (?5 IS NULL OR received_user_phone LIKE CONCAT('%', ?5, '%')) " +
           "AND (?6 IS NULL OR delivery_status = ?6) " +
           "AND (?7 IS NULL OR invoice_num LIKE CONCAT('%', ?7, '%')) " +
           "ORDER BY created_at DESC", 
           nativeQuery = true)
    Page<OrderItem> findOrdersByAdminConditionsWithPaging(
            String startDate, String endDate, String orderId,
            String receivedUserName, String receivedUserPhone, 
            String deliveryStatus, String invoiceNum, Pageable pageable);

    // 배송 상태 업데이트 - 위치기반으로 변경
    @Modifying
    @Query("UPDATE OrderItem oi SET oi.deliveryStatus = ?2 WHERE oi.orderId = ?1")
    int updateDeliveryStatus(Integer orderId, String deliveryStatus);
}