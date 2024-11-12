package com.d209.welight.domain.display.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayImageDto {
    private String displayImgUrl;
    private String displayImgColor;
    private Float displayImgScale;
    private Float displayImgRotation;
    private Float displayImgOffsetx;
    private Float displayImgOffsety;
}
