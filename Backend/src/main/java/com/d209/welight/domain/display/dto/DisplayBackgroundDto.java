package com.d209.welight.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayBackgroundDto {
    private Float displayBackgroundBrightness;  // 배경 밝기 추가
    private DisplayColorDto color;  // 배경 색상 정보
}