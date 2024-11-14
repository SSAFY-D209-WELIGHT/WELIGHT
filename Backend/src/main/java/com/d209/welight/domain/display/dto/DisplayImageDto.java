package com.d209.welight.domain.display.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayImageDto {
    @Schema(description = "이미지 URL", example = "https://example.com/cherry_blossom_full.jpg")
    private String displayImgUrl;

    @Schema(description = "이미지 색상", example = "#FFC0CB")
    private String displayImgColor;

    @Schema(description = "이미지 스케일", example = "1.2")
    private Float displayImgScale;

    @Schema(description = "이미지 회전 각도", example = "0")
    private Float displayImgRotation;

    @Schema(description = "이미지 X축 오프셋", example = "5")
    private Float displayImgOffsetx;

    @Schema(description = "이미지 Y축 오프셋", example = "10")
    private Float displayImgOffsety;
}
