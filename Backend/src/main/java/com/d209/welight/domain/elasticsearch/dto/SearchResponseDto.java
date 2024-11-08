package com.d209.welight.domain.elasticsearch.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponseDto {
    private Long displayUid;
    private String displayName;
    private String creatorNickname;
    private String displayThumbnailUrl;
    private List<String> tags;
    private Long likeCount;
    private Long downloadCount;
    private LocalDateTime createdAt;
} 