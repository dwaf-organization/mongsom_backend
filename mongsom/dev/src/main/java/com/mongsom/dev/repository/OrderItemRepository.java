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
    
    // 특정 사용자의 주문 조회 (최신순)
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = :userCode ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeOrderByCreatedAtDesc(@Param("userCode") Long userCode);
    
    // 특정 주문 상태의 주문들 조회
    @Query("SELECT o FROM OrderItem o WHERE o.deliveryStatus = :deliveryStatus ORDER BY o.createdAt DESC")
    List<OrderItem> findByDeliveryStatusOrderByCreatedAtDesc(@Param("deliveryStatus") String deliveryStatus);
    
    // 사용자의 특정 상태 주문 조회
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = :userCode AND o.deliveryStatus = :deliveryStatus ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeAndDeliveryStatusOrderByCreatedAtDesc(
            @Param("userCode") Long userCode, 
            @Param("deliveryStatus") String deliveryStatus);
    
    // 배송 현황 개수 조회
    @Query("SELECT COUNT(o) FROM OrderItem o WHERE o.userCode = :userCode AND o.deliveryStatus = :deliveryStatus")
    Integer countByUserCodeAndPaymentAtAfterAndDeliveryStatus(@Param("userCode") Long userCode, @Param("deliveryStatus") String deliveryStatus);

    // 사용자별 주문 기본 정보 조회 - paymentAt 내림차순
    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE oi.userCode = :userCode " +
           "ORDER BY oi.paymentAt DESC")
    List<OrderItem> findOrdersByUserCode(@Param("userCode") Long userCode);
    
    // 주문 ID로 배송 정보 조회 (택배회사, 송장번호)
    @Query(value = "SELECT delivery_com, invoice_num FROM order_item WHERE order_id = :orderId", nativeQuery = true)
    List<Object[]> findDeliveryInfoByOrderId(@Param("orderId") Integer orderId);
    
    // 주문 ID로 OrderItem 엔티티 조회
    Optional<OrderItem> findByOrderId(Integer orderId);
    
    // 날짜 범위로 주문 조회 (Native Query로 변경)
    @Query(value = "SELECT * FROM order_item WHERE DATE(payment_at) BETWEEN :startDate AND :endDate ORDER BY payment_at DESC", nativeQuery = true)
    List<OrderItem> findByPaymentAtBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // 주문번호 포함 검색 + 날짜 범위 (Native Query로 변경)
    @Query(value = "SELECT * FROM order_item WHERE CAST(order_id AS CHAR) LIKE %:orderId% AND DATE(payment_at) BETWEEN :startDate AND :endDate ORDER BY payment_at DESC", nativeQuery = true)
    List<OrderItem> findByOrderIdContainingAndPaymentAtBetween(@Param("orderId") String orderId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // 날짜 범위 + 페이징 (Native Query로 수정)
    @Query(value = "SELECT * FROM order_item " +
                   "WHERE DATE(payment_at) BETWEEN :startDate AND :endDate " +
                   "ORDER BY payment_at DESC", 
           nativeQuery = true)
    Page<OrderItem> findByPaymentAtBetweenWithPaging(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // 주문번호 포함 + 날짜 범위 + 페이징 (Native Query로 수정)
    @Query(value = "SELECT * FROM order_item " +
                   "WHERE CAST(order_id AS CHAR) LIKE CONCAT('%', :orderId, '%') " +
                   "AND DATE(payment_at) BETWEEN :startDate AND :endDate " +
                   "ORDER BY payment_at DESC", 
           nativeQuery = true)
    Page<OrderItem> findByOrderIdContainingAndPaymentAtBetweenWithPaging(
            @Param("orderId") String orderId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // 배송 상태 업데이트
    @Modifying
    @Query("UPDATE OrderItem oi SET oi.deliveryStatus = :deliveryStatus WHERE oi.orderId = :orderId")
    int updateDeliveryStatus(@Param("orderId") Integer orderId, @Param("deliveryStatus") String deliveryStatus);
}