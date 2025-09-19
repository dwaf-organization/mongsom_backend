package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mongsom.dev.entity.ChangeItem;

@Repository
public interface ChangeItemRepository extends JpaRepository<ChangeItem, Integer> {

    // orderItemId, orderId, userCode로 교환/반품 항목 조회
    @Query("SELECT ci FROM ChangeItem ci WHERE ci.orderItemId = :orderItemId AND ci.orderId = :orderId AND ci.userCode = :userCode")
    List<ChangeItem> findByOrderItemIdAndOrderIdAndUserCode(
            @Param("orderItemId") Integer orderItemId, 
            @Param("orderId") Integer orderId, 
            @Param("userCode") Integer userCode);
    
    // orderItemId, orderId, userCode로 교환/반품 항목 삭제
    @Modifying
    @Query("DELETE FROM ChangeItem ci WHERE ci.orderItemId = :orderItemId AND ci.orderId = :orderId AND ci.userCode = :userCode")
    int deleteByOrderItemIdAndOrderIdAndUserCode(
            @Param("orderItemId") Integer orderItemId, 
            @Param("orderId") Integer orderId, 
            @Param("userCode") Integer userCode);
    
}