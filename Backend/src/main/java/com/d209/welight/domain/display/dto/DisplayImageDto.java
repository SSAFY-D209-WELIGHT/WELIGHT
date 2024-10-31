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
    private String displayImgUrl;  // 이미 URL
    private String displayImgPosition;
}
