package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_IMAGE")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_IMG_UID")
    private Long displayImgUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(name = "DISPLAY_IMG_URL", nullable = false)
    private String displayImgUrl;

    @Column(name = "DISPLAY_IMG_COLOR", nullable = false)
    private String displayImgColor;

    @Column(name = "DISPLAY_IMG_SCALE", nullable = false)
    private Float displayImgScale;

    @Column(name = "DISPLAY_IMG_ROTATION", nullable = false)
    private Float displayImgRotation;

    @Column(name = "DISPLAY_IMG_OFFSETX", nullable = false)
    private Float displayImgOffsetx;

    @Column(name = "DISPLAY_IMG_OFFSETY", nullable = false)
    private Float displayImgOffsety;

    @Builder.Default
    @Column(name = "DISPLAY_IMG_CREATED_AT", nullable = false)
    private LocalDateTime displayImgCreatedAt = LocalDateTime.now();
}