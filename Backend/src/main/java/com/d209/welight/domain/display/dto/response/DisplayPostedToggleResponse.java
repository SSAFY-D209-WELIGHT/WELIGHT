package com.d209.welight.domain.display.dto.response;

import lombok.*;

@Getter
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayPostedToggleResponse {
    private Long displayUid;
    private Boolean displayIsPosted;
} 