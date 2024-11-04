package com.d209.welight.domain.display.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayCommentRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String commentText;

    // Optional 필드이므로 validation 불필요
    private Long parentCommentUid; // null OR 대댓글인 경우에만 값이 있음
}
