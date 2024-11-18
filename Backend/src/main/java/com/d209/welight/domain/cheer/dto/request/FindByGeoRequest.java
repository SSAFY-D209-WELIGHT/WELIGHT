package com.d209.welight.domain.cheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindByGeoRequest {
    //현재 내 중심좌표
    @Schema(description = "내 현재 위치 위도", example = "36.1073")
    private Double latitude;
    @Schema(description = "내 현재 위치 경도", example = "128.417")
    private Double longitude;
    @Schema(description = "현재 위치 반경 n km 내", example = "1")
    private Double radius; //반경 radius km내
}
