package com.d209.welight.domain.cheer.dto.response;

import com.d209.welight.domain.cheer.dto.CheerDisplayInfo;
import lombok.Builder;
import lombok.Getter;


import java.util.List;


@Getter
@Builder
public class CheerHistoryResponse {
    private String participationDate; 
    private String cheerroomName;
    private int participantCount;
    private String memo;
    private List<CheerDisplayInfo> displays;

    // Builder 패턴을 사용하기 위한 기본 생성자
    @Builder
    public CheerHistoryResponse(String participationDate,
                                String cheerroomName,
                                int participantCount,
                                String memo,
                                List<CheerDisplayInfo> displays) {
        this.participationDate = participationDate;
        this.cheerroomName = cheerroomName;
        this.participantCount = participantCount;
        this.memo = memo;
        this.displays = displays;
    }

}
