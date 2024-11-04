package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DISPLAY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Display {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_UID", columnDefinition = "BIGINT")
    private Long displayUid;

    @Column(name = "CREATOR_UID", nullable = false)
    private Long creatorUid;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String displayThumbnailUrl;

    @Column(nullable = false)
    private Boolean displayIsPosted;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime displayCreatedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private Long displayDownloadCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long displayLikeCount = 0L;

    // Display DisplayTag 엔티티 간의 일대다 관계
    // Display가 삭제되면 관련된 모든 DisplayTag도 함께 삭제
    @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisplayTag> tags;

    // Display 엔티티와 DisplayImage 엔티티 간의 일대다 관계를 설정
    // Display가 삭제되면 관련된 모든 DisplayImage도 함께 삭제
    @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisplayImage> images;

    // Display 엔티티와 DisplayText 엔티티 간의 일대다 관계를 설정
    // Display가 삭제되면 관련된 모든 DisplayText도 함께 삭제
    @OneToMany(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisplayText> texts;

    // Display 엔티티와 DisplayBackground 엔티티 간의 일대일 관계를 설정
    // Display가 삭제되면 관련된 DisplayBackground도 함께 삭제
    @OneToOne(mappedBy = "display", cascade = CascadeType.ALL, orphanRemoval = true)
    private DisplayBackground background;
}