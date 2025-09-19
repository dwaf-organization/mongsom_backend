package com.mongsom.dev.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdRespDto {

    private String userId;
    
    public static FindIdRespDto from(String userId) {
        return FindIdRespDto.builder()
                .userId(userId)
                .build();
    }
    
}
