package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mongsom.dev.entity.ChangeItem;

import jakarta.transaction.Transactional;

@Repository
public interface ChangeItemRepository extends JpaRepository<ChangeItem, Integer> {

    // orderItemId, orderId, userCode로 교환/반품 항목 조회
    @Query("SELECT ci FROM ChangeItem ci WHERE ci.orderDetailId = :orderDetailId AND ci.orderId = :orderId AND ci.userCode = :userCode")
    List<ChangeItem> findByOrderItemIdAndOrderIdAndUserCode(
            @Param("orderDetailId") Integer orderDetailId, 
            @Param("orderId") Integer orderId, 
            @Param("userCode") Long userCode);
    
    // orderItemId, orderId, userCode로 교환/반품 항목 삭제
    @Modifying
    @Query("DELETE FROM ChangeItem ci WHERE ci.orderDetailId = :orderDetailId AND ci.orderId = :orderId AND ci.userCode = :userCode")
    int deleteByOrderItemIdAndOrderIdAndUserCode(
            @Param("orderDetailId") Integer orderDetailId, 
            @Param("orderId") Integer orderId, 
            @Param("userCode") Long userCode);
    
    
    @Query(value = "SELECT order_detail_id, change_status " +
                   "FROM change_item WHERE order_detail_id IN (:orderDetailIds)", 
           nativeQuery = true)
    List<Object[]> findChangeStatusByOrderDetailIds(@Param("orderDetailIds") List<Integer> orderDetailIds);
    
    // 교환/반품 상품 목록 조회 (복합 조인 쿼리)
    @Query(value = "SELECT " +
            "ci.change_id, ci.order_detail_id, ci.order_id, ci.user_code, " +
            "ci.change_status, ci.approval_status, ci.contents, " +
            "oi.payment_at, oi.received_user_name, " +
            "od.price, " +
            "p.name, " +
            "po.opt_name, " +
            "GROUP_CONCAT(pi.product_img_url SEPARATOR ',') " +
            "FROM change_item ci " +
            "JOIN order_item oi ON ci.order_id = oi.order_id " +
            "JOIN order_detail od ON ci.order_detail_id = od.order_detail_id " +
            "JOIN product p ON od.product_id = p.product_id " +
            "LEFT JOIN product_option po ON od.opt_id = po.opt_id " +
            "LEFT JOIN product_img pi ON p.product_id = pi.product_id " +
            "WHERE ci.change_status = :changeStatus " +
            "GROUP BY ci.change_id, ci.order_detail_id, ci.order_id, ci.user_code, " +
            "ci.change_status, ci.approval_status, ci.contents, " +
            "oi.payment_at, oi.received_user_name, od.price, p.name, po.opt_name " +
            "ORDER BY ci.created_at DESC " +
            "LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}",
            nativeQuery = true)
    List<Object[]> findChangeProductListByStatus(@Param("changeStatus") Integer changeStatus, 
                                                 Pageable pageable);
    
    // 교환/반품 상태별 총 개수 조회
    @Query("SELECT COUNT(ci) FROM ChangeItem ci WHERE ci.changeStatus = :changeStatus")
    long countByChangeStatus(@Param("changeStatus") Integer changeStatus);
    
    // 승인 상태 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE ChangeItem ci SET ci.approvalStatus = :approvalStatus WHERE ci.changeId = :changeId")
    int updateApprovalStatus(@Param("changeId") Integer changeId, @Param("approvalStatus") Integer approvalStatus);
    
}