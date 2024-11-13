package com.d209.welight.domain.display.dto.request;

import com.d209.welight.domain.display.dto.DisplayBackgroundDto;
import com.d209.welight.domain.display.dto.DisplayImageDto;
import com.d209.welight.domain.display.dto.DisplayTextDto;
import com.d209.welight.domain.display.entity.DisplayImage;
import com.d209.welight.domain.display.entity.DisplayText;
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
    private String displayName;  // 디스플레이 이름
    private String displayThumbnailUrl;  // 썸네일 URL
    private Boolean displayIsPosted;  // 게시 여부
    private List<String> tags;  // 태그 목록
    private List<DisplayImageDto> images;  // 이미지 정보
    private List<DisplayTextDto> texts;  // 텍스트 정보
    private DisplayBackgroundDto background;  // 배경 정보
}