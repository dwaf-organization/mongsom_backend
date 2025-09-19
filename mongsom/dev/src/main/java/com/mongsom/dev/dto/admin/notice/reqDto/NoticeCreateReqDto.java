package com.mongsom.dev.dto.admin.notice.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateReqDto {
    
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하로 입력해주세요.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String contents;
}