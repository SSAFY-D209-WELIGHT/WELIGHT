package com.d209.welight.domain.cheer.dto.response;

import java.util.List;

import com.d209.welight.domain.cheer.dto.CheerDisplayInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class CheerHistoryDetailResponse {
    private String participationDate;  // "7 AM" 형식
    private String cheerroomName;
    private int participantCount;
    private String memo;
    private List<CheerDisplayInfo> displays;
    
    // 상세 정보 추가
    private String totalDuration;        // 응원 시간 (분 단위)
    private Double latitude;           // 위도
    private Double longitude;          // 경도


} 