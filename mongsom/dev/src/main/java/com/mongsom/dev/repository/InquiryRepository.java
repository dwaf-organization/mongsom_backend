package com.mongsom.dev.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.mongsom.dev.entity.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    
    // 모든 견적문의 조회 (최신순)
    @Query("SELECT i FROM Inquiry i ORDER BY i.createdAt DESC")
    List<Inquiry> findAllOrderByCreatedAtDesc();
    
    /**
     * 견적문의 페이지네이션 조회 (최신순)
     */
    @Query("SELECT i FROM Inquiry i ORDER BY i.createdAt DESC")
    Page<Inquiry> findAllWithPagination(Pageable pageable);
}