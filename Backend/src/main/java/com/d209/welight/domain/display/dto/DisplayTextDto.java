package com.d209.welight.domain.display.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTextDto implements Serializable {

    @Schema(description = "텍스트 내용", example = "아름다운 봄, 벚꽃 축제에 여러분을 초대합니다")
    private String displayTextDetail; // 텍스트 내용 (VARCHAR(255))

    @Schema(description = "텍스트 색상", example = "#FF1493")
    private String displayTextColor; // 텍스트 색상 (CHAR(9))

    @Schema(description = "텍스트 폰트", example = "나눔고딕")
    private String displayTextFont; // 텍스트 폰트 (VARCHAR(50))

    @Schema(description = "텍스트 회전 각도", example = "0")
    private Float displayTextRotation; // 텍스트 회전

    @Schema(description = "텍스트 스케일", example = "1.1")
    private Float displayTextScale;

    @Schema(description = "텍스트 X축 오프셋", example = "15")
    private Float displayTextOffsetx;

    @Schema(description = "텍스트 Y축 오프셋", example = "25")
    private Float displayTextOffsety;
}
