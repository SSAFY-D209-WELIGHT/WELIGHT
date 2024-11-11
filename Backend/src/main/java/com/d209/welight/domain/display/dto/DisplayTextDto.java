package com.d209.welight.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTextDto {
    private String displayTextDetail;  // 텍스트 내용 (VARCHAR(255))
    private String displayTextColor;  // 텍스트 색상 (CHAR(9))
    private String displayTextFont;  // 텍스트 폰트 (VARCHAR(50))
    private Float displayTextRotation;  // 텍스트 회전
    private Float displayTextScale;
    private Float displayTextOffsetx;
    private Float displayTextOffsety;
}
