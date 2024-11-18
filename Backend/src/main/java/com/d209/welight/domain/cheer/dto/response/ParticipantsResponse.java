package com.d209.welight.domain.cheer.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantsResponse {
    /* 응원방 내에 참여중인 멤버들 목록 */
    private String userNickname;
    private String userProfileImg;
    private boolean isLeader; // 현재 이 사용자가 이 그룹의 방장인지 여부
}
