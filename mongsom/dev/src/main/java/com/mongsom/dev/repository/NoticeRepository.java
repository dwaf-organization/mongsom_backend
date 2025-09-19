package com.mongsom.dev.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mongsom.dev.entity.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    
    // 모든 공지사항 조회 (최신순, 페이징 지원)
    @Query("SELECT n FROM Notice n ORDER BY n.createdAt DESC")
    Page<Notice> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    // 제목으로 검색 (페이징 지원)
    @Query("SELECT n FROM Notice n WHERE n.title LIKE %:keyword% ORDER BY n.createdAt DESC")
    Page<Notice> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // 작성자로 검색 (페이징 지원)
    @Query("SELECT n FROM Notice n WHERE n.writer LIKE %:writer% ORDER BY n.createdAt DESC")
    Page<Notice> findByWriterContaining(@Param("writer") String writer, Pageable pageable);
    
    // 제목 또는 내용으로 검색 (페이징 지원)
    @Query("SELECT n FROM Notice n WHERE n.title LIKE %:keyword% OR n.contents LIKE %:keyword% ORDER BY n.createdAt DESC")
    Page<Notice> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);
}