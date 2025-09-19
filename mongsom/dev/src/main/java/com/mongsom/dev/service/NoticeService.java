package com.mongsom.dev.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.notice.respDto.NoticeDetailRespDto;
import com.mongsom.dev.dto.notice.respDto.NoticeRespDto;
import com.mongsom.dev.entity.Notice;
import com.mongsom.dev.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    
    private final NoticeRepository noticeRepository;
    
    //공지사항리스트조회
    @Transactional(readOnly = true)
    public RespDto<NoticeRespDto> getAllNotices(Pageable pageable) {
        try {
            log.info("공지사항 조회 시작 - page: {}, size: {}", 
                    pageable.getPageNumber() + 1, pageable.getPageSize());
            
            Page<Notice> noticePage = noticeRepository.findAllOrderByCreatedAtDesc(pageable);
            
            NoticeRespDto noticeRespDto = NoticeRespDto.from(noticePage);
            
            log.info("공지사항 조회 완료 - totalElements: {}, totalPages: {}, currentPage: {}", 
                    noticePage.getTotalElements(), noticePage.getTotalPages(), 
                    noticePage.getNumber() + 1);
            
            return RespDto.<NoticeRespDto>builder()
                    .code(1)
                    .data(noticeRespDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("공지사항 조회 실패 - page: {}, size: {}", 
                    pageable.getPageNumber() + 1, pageable.getPageSize(), e);
            return RespDto.<NoticeRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
    //공지사항상세조회
    @Transactional(readOnly = true)
    public RespDto<NoticeDetailRespDto> getNoticeDetail(Integer noticeId) {
        try {
            log.info("공지사항 상세 조회 시작 - noticeId: {}", noticeId);
            
            Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
            
            if (noticeOpt.isEmpty()) {
                log.warn("존재하지 않는 공지사항 - noticeId: {}", noticeId);
                return RespDto.<NoticeDetailRespDto>builder()
                        .code(-1)
                        .data(null)
                        .build();
            }
            
            Notice notice = noticeOpt.get();
            NoticeDetailRespDto noticeDetailRespDto = NoticeDetailRespDto.from(notice);
            
            log.info("공지사항 상세 조회 완료 - noticeId: {}, title: {}", 
                    noticeId, notice.getTitle());
            
            return RespDto.<NoticeDetailRespDto>builder()
                    .code(1)
                    .data(noticeDetailRespDto)
                    .build();
                    
        } catch (Exception e) {
            log.error("공지사항 상세 조회 실패 - noticeId: {}", noticeId, e);
            return RespDto.<NoticeDetailRespDto>builder()
                    .code(-1)
                    .data(null)
                    .build();
        }
    }
}