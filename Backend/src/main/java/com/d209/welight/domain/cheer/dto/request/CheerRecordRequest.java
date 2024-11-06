package com.d209.welight.domain.cheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheerRecordRequest {
    @Schema(description = "응원 기록용 메모", example = "기다리던 공연 너무 재밌었다!")
    private String cheerMemo;
}
