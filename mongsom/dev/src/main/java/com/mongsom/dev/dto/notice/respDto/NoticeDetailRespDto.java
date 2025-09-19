package com.mongsom.dev.dto.notice.respDto;

import java.time.LocalDateTime;

import com.mongsom.dev.entity.Notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailRespDto {
    
    private Integer noticeId;
    private String title;
    private String contents;
    private String writer;
    private LocalDateTime createdAt;
    
    public static NoticeDetailRespDto from(Notice notice) {
        return NoticeDetailRespDto.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .contents(notice.getContents())
                .writer(notice.getWriter())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}