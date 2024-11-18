package com.d209.welight.domain.cheer.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheerroomCreateRequest {
    @Schema(description = "응원방 이름", example = "jy's cheerroom")
    private String cheerroomName;

    @Schema(description = "응원방 번호", example = "1")
    private Long cheerroomNumber;

    @Schema(description = "응원방 설명", example = "test방 설명")
    private String cheerroomDescription;

    @Schema(description = "위도", example = "45.4")
    private Double latitude;

    @Schema(description = "경도", example = "90.3")
    private Double longitude;




}