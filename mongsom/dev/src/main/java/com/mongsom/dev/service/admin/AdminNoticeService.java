package com.mongsom.dev.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeCreateReqDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeUpdateReqDto;
import com.mongsom.dev.entity.Notice;
import com.mongsom.dev.repository.NoticeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminNoticeService {
    
    private final NoticeRepository noticeRepository;
    
    // 공지사항 등록
    @Transactional
    public RespDto<Boolean> createNotice(NoticeCreateReqDto reqDto) {
        try {
            Notice notice = Notice.builder()
                    .title(reqDto.getTitle())
                    .contents(reqDto.getContents())
                    .writer("관리자")
                    .build();
            
            noticeRepository.save(notice);
            
            log.info("공지사항 등록 완료 - title: {}", reqDto.getTitle());
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
                    
        } catch (Exception e) {
            log.error("공지사항 등록 실패 - title: {}, error: {}", reqDto.getTitle(), e.getMessage());
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    // 공지사항 수정
    @Transactional
    public RespDto<Boolean> updateNotice(Integer noticeId, NoticeUpdateReqDto reqDto) {
        try {
            log.info("=== 공지사항 수정 시작 - noticeId: {} ===", noticeId);
            
            // 1. 공지사항 조회
            Optional<Notice> noticeOpt = noticeRepository.findById(noticeId);
            
            if (noticeOpt.isEmpty()) {
                log.warn("존재하지 않는 공지사항 - noticeId: {}", noticeId);
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            Notice notice = noticeOpt.get();
            
            // 2. 공지사항 수정
            notice.setTitle(reqDto.getTitle());
            notice.setContents(reqDto.getContents());
            // updatedAt은 @UpdateTimestamp로 자동 갱신
            
            noticeRepository.save(notice);
            
            log.info("=== 공지사항 수정 완료 - noticeId: {} ===", noticeId);
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
            
        } catch (Exception e) {
            log.error("공지사항 수정 실패 - noticeId: {}, error: {}", 
                    noticeId, e.getMessage(), e);
            
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
    // 공지사항 삭제
    @Transactional
    public RespDto<Boolean> deleteNotice(Integer noticeId) {
        try {
            log.info("=== 공지사항 삭제 시작 - noticeId: {} ===", noticeId);
            
            // 1. 공지사항 존재 확인
            if (!noticeRepository.existsById(noticeId)) {
                log.warn("존재하지 않는 공지사항 - noticeId: {}", noticeId);
                return RespDto.<Boolean>builder()
                        .code(-1)
                        .data(false)
                        .build();
            }
            
            // 2. 공지사항 삭제
            noticeRepository.deleteById(noticeId);
            
            log.info("=== 공지사항 삭제 완료 - noticeId: {} ===", noticeId);
            
            return RespDto.<Boolean>builder()
                    .code(1)
                    .data(true)
                    .build();
            
        } catch (Exception e) {
            log.error("공지사항 삭제 실패 - noticeId: {}, error: {}", 
                    noticeId, e.getMessage(), e);
            
            return RespDto.<Boolean>builder()
                    .code(-1)
                    .data(false)
                    .build();
        }
    }
    
}