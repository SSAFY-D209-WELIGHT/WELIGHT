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
    private String displayTextColor;  // 텍스트 색상 (CHAR(7))
    private String displayTextFont;  // 텍스트 폰트 (VARCHAR(50))
    private Float displayTextRotation;  // 텍스트 회전
    private String displayTextPosition;  // 텍스트 위치 (VARCHAR(50))
}
