package com.d209.welight.domain.display.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DisplayPostedToggleResponse {
    private Long displayUid;
    private Boolean displayIsPosted;
} 