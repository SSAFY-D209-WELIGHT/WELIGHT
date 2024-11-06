package com.d209.welight.domain.cheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LeaderDelegateRequest {
    @Schema(description = "위임할 방장의 user UID값", example = "1")
    private Long userUid;
}
