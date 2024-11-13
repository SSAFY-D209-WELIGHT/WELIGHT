package com.d209.welight.domain.display.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "댓글 내용", example = "댓글 예시입니다.")
    private String commentText;

    // Optional 필드이므로 validation 불필요
    @Schema(description = "부모 댓글 UID", example = "1")
    private Long parentCommentUid; // null OR 대댓글인 경우에만 값이 있음
}
