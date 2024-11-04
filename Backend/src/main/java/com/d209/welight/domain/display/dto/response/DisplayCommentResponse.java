package com.d209.welight.domain.display.dto.response;

import com.d209.welight.domain.display.entity.DisplayComment;
import com.d209.welight.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayCommentResponse {
    private Long commentUid;
//    private Long userUid;
    private boolean isMyComment; //내가 쓴 글 여부
    private String userNickname;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long parentCommentUid; //null일 수도 있음
    private List<DisplayCommentResponse> replies;

    public static DisplayCommentResponse convertToDTO(DisplayComment comment, User currentUser) {
        if (comment == null) return null;

        List<DisplayCommentResponse> replies = comment.getChildComments().stream()
                .map(reply -> DisplayCommentResponse.convertToDTO(reply, currentUser))
                .collect(Collectors.toList());

        return DisplayCommentResponse.builder()
                .commentUid(comment.getCommentUid())
//                .userUid(comment.getUser().getUserUid())
                .userNickname(comment.getUser().getUserNickname())
                .commentText(comment.getCommentText())
                .createdAt(comment.getCommentCreatedAt())
                .updatedAt(comment.getCommentUpdatedAt())
                .parentCommentUid(comment.getParentComment() != null ?
                        comment.getParentComment().getCommentUid() : null)
                .replies(replies)
                .isMyComment(isCommentOwner(comment, currentUser))
                .build();
    }

    private static boolean isCommentOwner(DisplayComment comment, User currentUser) {
        if (currentUser == null) return false;
        return comment.getUser().equals(currentUser);
    }
}
