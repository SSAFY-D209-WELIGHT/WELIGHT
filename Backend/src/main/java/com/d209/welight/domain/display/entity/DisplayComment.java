package com.d209.welight.domain.display.entity;

import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DISPLAY_COMMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_UID")
    private Long commentUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_UID")
    private DisplayComment parentComment;

    // cascade 설정으로 부모 댓글 삭제 시 대댓글도 함께 삭제
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<DisplayComment> childComments = new ArrayList<>();

    @Column(name = "COMMENT_TEXT", nullable = false, length = 255)
    private String commentText;

    @Column(name = "COMMENT_CREATED_AT", nullable = false)
    private LocalDateTime commentCreatedAt;

    @Column(name = "COMMENT_UPDATED_AT")
    private LocalDateTime commentUpdatedAt;
}