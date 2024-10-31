package com.d209.welight.domain.display.dto.request;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DisplayDetailRequest {

    private Long displayUid; // 디스플레이 ID
    private Long userId;  // 현재 유저 ID

}
