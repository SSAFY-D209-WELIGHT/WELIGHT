package com.d209.welight.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayColorDto {
    private String displayColorSolid;  // 단색 배경 (#RRGGBB 형식)
    private String displayBackgroundGradationColor1;  // 그라데이션 색상 1 (#RRGGBB 형식)
    private String displayBackgroundGradationColor2;  // 그라데이션 색상 2 (#RRGGBB 형식)
    private String displayBackgroundGradationType;  // 그라데이션 타입 (VARCHAR(50))
}
