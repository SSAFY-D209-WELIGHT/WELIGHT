package com.d209.welight.domain.display.dto.request;

import com.d209.welight.domain.display.dto.DisplayBackgroundDto;
import com.d209.welight.domain.display.dto.DisplayImageDto;
import com.d209.welight.domain.display.dto.DisplayTextDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    //    private Long creatorUid;  // 생성자 ID -> header 사용
    @Schema(description = "디스플레이 이름", example = "봄의 벚꽃 축제")
    private String displayName;  // 디스플레이 이름

    @Schema(description = "디스플레이 썸네일 URL", example = "https://example.com/cherry_blossom_thumbnail.jpg")
    private String displayThumbnailUrl;  // 썸네일 URL

    @Schema(description = "디스플레이 게시 여부", example = "true")
    private Boolean displayIsPosted;  // 게시 여부

    @Schema(description = "디스플레이 태그 목록", example = "[\"봄\", \"벚꽃\", \"축제\", \"자연\"]")
    private List<String> tags;  // 태그 목록

    @Schema(description = "디스플레이 이미지 정보 목록")
    private List<DisplayImageDto> images;  // 이미지 정보

    @Schema(description = "디스플레이 텍스트 정보 목록")
    private List<DisplayTextDto> texts;  // 텍스트 정보

    @Schema(description = "디스플레이 배경 정보")
    private DisplayBackgroundDto background;  // 배경 정보
}