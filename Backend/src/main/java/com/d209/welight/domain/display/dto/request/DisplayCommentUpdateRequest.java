package com.d209.welight.domain.display.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayCommentUpdateRequest {
    private Long commentUid;
    private String newCommentText;
}
