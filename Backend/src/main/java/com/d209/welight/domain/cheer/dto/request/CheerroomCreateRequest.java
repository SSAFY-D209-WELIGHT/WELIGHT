package com.d209.welight.domain.cheer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CheerroomCreateRequest {
    private String cheerroomName;
    private Double latitude;
    private Double longitude;
}