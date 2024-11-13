package com.d209.welight.domain.cheer.dto.response;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CheerroomResponse {
    private Long cheerroomUid;
    private String cheerroomName;
    private String cheerroomDescription;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;
    private int participantCount;

    public static CheerroomResponse from(Cheerroom cheerroom, int participantCount) {
        return CheerroomResponse.builder()
                .cheerroomUid(cheerroom.getId())
                .cheerroomName(cheerroom.getName())
                .cheerroomDescription(cheerroom.getDescription())
                .latitude(cheerroom.getLatitude())
                .longitude(cheerroom.getLongitude())
                .createdAt(cheerroom.getCreatedAt())
                .participantCount(participantCount)
                .build();
    }
}
