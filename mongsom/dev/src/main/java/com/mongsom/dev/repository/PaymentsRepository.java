package com.mongsom.dev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.Payments;

@Repository
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
    
    // 주문 ID와 사용자 코드로 결제 정보 조회
    Optional<Payments> findByOrderIdAndUserCode(Integer orderId, Long userCode);
    
    // 특정 주문의 결제 정보 조회
    @Query("SELECT p FROM Payments p WHERE p.orderId = :orderId")
    List<Payments> findByOrderId(@Param("orderId") Integer orderId);
    
    // 특정 사용자의 결제 내역 조회
    @Query("SELECT p FROM Payments p WHERE p.userCode = :userCode ORDER BY p.createdAt DESC")
    List<Payments> findByUserCodeOrderByCreatedAtDesc(@Param("userCode") Long userCode);
    
    // 결제 상태별 조회
    @Query("SELECT p FROM Payments p WHERE p.paymentStatus = :paymentStatus ORDER BY p.createdAt DESC")
    List<Payments> findByPaymentStatusOrderByCreatedAtDesc(@Param("paymentStatus") String paymentStatus);
    
    // 결제 키로 조회 (PG사 연동용)
    @Query("SELECT p FROM Payments p WHERE p.paymentKey = :paymentKey")
    Optional<Payments> findByPaymentKey(@Param("paymentKey") String paymentKey);
    
//    // 주문 ID로 결제 정보 조회 (단일 Object[] 반환)
//    @Query(value = "SELECT payment_method, payment_amount, payment_status, pg_provider " +
//                   "FROM payments WHERE order_id = :orderId LIMIT 1", nativeQuery = true)
//    Optional<Payments> findPaymentInfoByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT p FROM Payments p WHERE p.orderId = :orderId")
    Optional<Payments> findPaymentInfoByOrderId(@Param("orderId") Integer orderId);
    
    // 주문 ID로 결제 정보 존재 확인
    @Query(value = "SELECT COUNT(*) FROM payments WHERE order_id = :orderId", nativeQuery = true)
    Integer countByOrderId(@Param("orderId") Integer orderId);

    // 결제 정보 업데이트
    @Modifying
    @Query("UPDATE Payments p SET " +
           "p.paymentMethod = :paymentMethod, " +
           "p.paymentStatus = :paymentStatus, " +
           "p.paymentKey = :paymentKey, " +
           "p.pgProvider = :pgProvider " +
           "WHERE p.orderId = :orderId AND p.userCode = :userCode")
    int updatePaymentInfo(@Param("orderId") Integer orderId,
                         @Param("userCode") Long userCode,
                         @Param("paymentMethod") String paymentMethod,
                         @Param("paymentStatus") String paymentStatus,
                         @Param("paymentKey") String paymentKey,
                         @Param("pgProvider") String pgProvider);

}