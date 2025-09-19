package com.mongsom.dev.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(staticName="set")
public class RespDto<D> {
    private int code;
    private D data;

    public static <D> RespDto<D> success(String message, D data) {
        return RespDto.<D>builder()
                .code(1)
                .data(data)
                .build();
    }
    public static <D> RespDto<D> fail(String message) {
        return RespDto.<D>builder()
                .code(-1)
                .data(null)
                .build();
    }
}
