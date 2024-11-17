package com.d209.welight.domain.display.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayBackgroundDto implements Serializable {

    @Schema(description = "배경 밝기", example = "0.9")
    private Float displayBackgroundBrightness; // 배경 밝기 추가

    @Schema(description = "단색 배경 색상", example = "#FFFFFF")
    private String displayColorSolid; // 단색 배경 (#RRGGBB 형식)

    @Schema(description = "그라데이션 색상 1", example = "#FFB6C1")
    private String displayBackgroundGradationColor1; // 그라데이션 색상 1 (#RRGGBB 형식)

    @Schema(description = "그라데이션 색상 2", example = "#FFF0F5")
    private String displayBackgroundGradationColor2; // 그라데이션 색상 2 (#RRGGBB 형식)

    @Schema(description = "그라데이션 타입", example = "radial")
    private String displayBackgroundGradationType; // 그라데이션 타입 (VARCHAR(50))
}