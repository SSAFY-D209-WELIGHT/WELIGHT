package com.d209.welight.domain.display.entity;

import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Display {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayUid;

    @ManyToOne
    @JoinColumn(name = "CREATOR_UID", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String displayThumbnailUrl;

    @Column(nullable = false)
    private Boolean displayIsPosted;

    @Column(nullable = false)
    private LocalDateTime displayUpdatedAt;

    @Column(nullable = false)
    private LocalDateTime displayCreatedAt;

    @Column(nullable = false)
    private Long displayDownloadCount = 0L;

    @Column(nullable = false)
    private Long displayLikeCount = 0L;
}