package com.mongsom.dev.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
    // 특정 사용자의 주문 조회 (최신순)
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = :userCode ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeOrderByCreatedAtDesc(@Param("userCode") Integer userCode);
    
    // 특정 주문 상태의 주문들 조회
    @Query("SELECT o FROM OrderItem o WHERE o.deliveryStatus = :deliveryStatus ORDER BY o.createdAt DESC")
    List<OrderItem> findByDeliveryStatusOrderByCreatedAtDesc(@Param("deliveryStatus") String deliveryStatus);
    
    // 사용자의 특정 상태 주문 조회
    @Query("SELECT o FROM OrderItem o WHERE o.userCode = :userCode AND o.deliveryStatus = :deliveryStatus ORDER BY o.createdAt DESC")
    List<OrderItem> findByUserCodeAndDeliveryStatusOrderByCreatedAtDesc(
            @Param("userCode") Integer userCode, 
            @Param("deliveryStatus") String deliveryStatus);
    
    // 3개월 이내 주문 중 특정 배송 상태별 개수 조회 (네이티브 쿼리 사용)
    @Query(value = "SELECT COUNT(*) FROM order_item " +
           "WHERE user_code = :userCode " +
           "AND payment_at >= DATE_SUB(NOW(), INTERVAL 3 MONTH) " +
           "AND delivery_status = :deliveryStatus", nativeQuery = true)
    Integer countByUserCodeAndPaymentAtAfterAndDeliveryStatus(
            @Param("userCode") Integer userCode,
            @Param("deliveryStatus") String deliveryStatus);

    // 사용자별 주문 기본 정보 조회 - paymentAt 내림차순
    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE oi.userCode = :userCode " +
           "ORDER BY oi.paymentAt DESC")
    List<OrderItem> findOrdersByUserCode(@Param("userCode") Integer userCode);
    
    // 주문 ID로 배송 정보 조회 (택배회사, 송장번호)
    @Query(value = "SELECT delivery_com, invoice_num FROM order_item WHERE order_id = :orderId", nativeQuery = true)
    List<Object[]> findDeliveryInfoByOrderId(@Param("orderId") Integer orderId);
    
    // 주문 ID로 OrderItem 엔티티 조회
    Optional<OrderItem> findByOrderId(Integer orderId);
}