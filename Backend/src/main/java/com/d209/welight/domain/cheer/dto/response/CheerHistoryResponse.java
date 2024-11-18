package com.d209.welight.domain.cheer.dto.response;

import com.d209.welight.domain.cheer.dto.CheerDisplayInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Setter
@Getter
@Builder
public class CheerHistoryResponse {
    private String participationDate; 
    private String cheerroomName;
    private int participantCount;
    private String memo;
    private List<CheerDisplayInfo> displays;
}
