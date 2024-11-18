package com.d209.welight.domain.display.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayCreateResponse {

    private Long displayUid;  // 생성된 디스플레이 ID
    private String displayName;  // 디스플레이 이름
    private String message;  // 성공 메시지


} 