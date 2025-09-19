package com.mongsom.dev.service.admin;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mongsom.dev.common.dto.RespDto;
import com.mongsom.dev.dto.admin.notice.reqDto.NoticeCreateReqDto;
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
}