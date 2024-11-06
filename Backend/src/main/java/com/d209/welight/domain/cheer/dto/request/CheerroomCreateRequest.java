package com.d209.welight.domain.cheer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CheerroomCreateRequest {
    private String cheerroomName;
    private Double latitude;
    private Double longitude;
}