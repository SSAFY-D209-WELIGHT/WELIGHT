package com.d209.welight.domain.cheer.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CheerDisplayInfo {
    private Long displayUid;
    private String displayName;
    private String thumbnailUrl;
    private String usedAt;
}
