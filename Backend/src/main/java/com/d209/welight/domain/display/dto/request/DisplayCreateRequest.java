package com.d209.welight.domain.display.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayCreateRequest {
    private Long creatorUid;  // 생성자 ID
    private String displayName;  // 디스플레이 이름
    private String displayThumbnailUrl;  // 썸네일 URL
    private Boolean displayIsPosted;  // 게시 여부
    private List<String> tags;  // 태그 목록
    private List<DisplayImageDto> images;  // 이미지 정보
    private List<DisplayTextDto> texts;  // 텍스트 정보
    private DisplayBackgroundDto background;  // 배경 정보

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayImageDto {
        private String displayImgUrl;  // 이미 URL
        private String displayImgPosition;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayTextDto {
        private String displayTextDetail;  // 텍스트 내용 (VARCHAR(255))
        private String displayTextColor;  // 텍스트 색상 (CHAR(7))
        private String displayTextFont;  // 텍스트 폰트 (VARCHAR(50))
        private Float displayTextRotation;  // 텍스트 회전
        private String displayTextPosition;  // 텍스트 위치 (VARCHAR(50))
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayBackgroundDto {
        private Float displayBackgroundBrightness;  // 배경 밝기 추가
        private DisplayColorDto color;  // 배경 색상 정보
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayColorDto {
        private String displayColorSolid;  // 단색 배경 (#RRGGBB 형식)
        private String displayBackgroundGradationColor1;  // 그라데이션 색상 1 (#RRGGBB 형식)
        private String displayBackgroundGradationColor2;  // 그라데이션 색상 2 (#RRGGBB 형식)
        private String displayBackgroundGradationType;  // 그라데이션 타입 (VARCHAR(50))
    }
} 