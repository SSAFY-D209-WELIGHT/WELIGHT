package com.d209.welight.domain.display.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayCommentRequest {
    private Long displayUid;
    private String commentText;
    private Long parentCommentUid; // null OR 대댓글인 경우에만 값이 있음
}
