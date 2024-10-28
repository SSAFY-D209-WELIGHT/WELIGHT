package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@Table(name = "DISPLAY")
@NoArgsConstructor
@AllArgsConstructor
public class Display {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_UID")
    private Long displayUid;

    @Column(name = "CREATOR_UID", nullable = false)
    private Long creatorUid;

    @Column(name = "DISPLAY_NAME", nullable = false)
    private String displayName;

    @Column(name = "DISPLAY_THUMBNAIL_URL", nullable = false)
    private String displayThumbnailUrl;

    @Column(name = "DISPLAY_IS_POSTED", nullable = false)
    private Boolean displayIsPosted;

    @Column(name = "DISPLAY_CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date displayCreatedAt;

    @Column(name = "DISPLAY_UPDATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date displayUpdatedAt;

    @Column(name = "DISPLAY_DOWNLOAD_COUNT", nullable = false)
    private Long displayDownloadCount;

    @Column(name = "DISPLAY_LIKE_COUNT", nullable = false)
    private Long displayLikeCount;

}