package com.d209.welight.domain.display.entity;

import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_COMMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentUid;

    @ManyToOne
    @JoinColumn(name = "USER_UID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @ManyToOne
    @JoinColumn(name = "PARENT_COMMENT_UID")
    private DisplayComment parentComment;

    @Column(nullable = false)
    private String commentText;

    @Column(nullable = false)
    private LocalDateTime commentCreatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime commentUpdatedAt = LocalDateTime.now();
}