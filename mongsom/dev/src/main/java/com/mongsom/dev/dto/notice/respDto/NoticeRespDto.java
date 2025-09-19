package com.mongsom.dev.dto.notice.respDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

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
public class NoticeRespDto {
    
    private List<NoticeItemDto> items;
    private PaginationDto pagination;
    
    public static NoticeRespDto from(Page<Notice> noticePage) {
        List<NoticeItemDto> items = noticePage.getContent().stream()
                .map(NoticeItemDto::from)
                .collect(Collectors.toList());
        
        PaginationDto pagination = PaginationDto.from(noticePage);
        
        return NoticeRespDto.builder()
                .items(items)
                .pagination(pagination)
                .build();
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeItemDto {
        private Integer noticeId;
        private String title;
        private String writer;
        private LocalDateTime createdAt;
        
        public static NoticeItemDto from(Notice notice) {
            return NoticeItemDto.builder()
                    .noticeId(notice.getNoticeId())
                    .title(notice.getTitle())
                    .writer(notice.getWriter())
                    .createdAt(notice.getCreatedAt())
                    .build();
        }
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaginationDto {
        private Integer currentPage;
        private Integer totalPage;
        private Integer size;
        private Boolean hasNext;
        
        public static PaginationDto from(Page<?> page) {
            return PaginationDto.builder()
                    .currentPage(page.getNumber() + 1)
                    .totalPage(page.getTotalPages())
                    .size(page.getSize())
                    .hasNext(page.hasNext())
                    .build();
        }
    }
}