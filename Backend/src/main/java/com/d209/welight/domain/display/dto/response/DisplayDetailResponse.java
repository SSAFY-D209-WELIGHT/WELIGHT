package com.d209.welight.domain.display.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayDetailResponse implements Serializable {
    private Long creatorUid;  // 제작자 고유번호
    private String creatorName; // 제작자 이름
    private String displayName;  // 디스플레이 이름
    private String displayThumbnailUrl;  // 썸네일 URL
    private Boolean displayIsPosted;  // 게시 여부
    private List<String> tags;  // 태그 목록
    private boolean isOwner; // 소유 여부 (현재 사용자와 제작자의 uid 비교)
    private boolean isFavorite; // 즐겨찾기 여부
    private boolean isLiked; // 좋아요 여부

    @Builder.Default
    private Long likeCount = 0L; // 좋아요 수
    @Builder.Default
    private Long downloadCount = 0L; // 다운로드 수
    @Builder.Default
    private Long commentCount = 0L; // 댓글 수
}
