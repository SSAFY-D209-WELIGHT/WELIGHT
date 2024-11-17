package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_TEXT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayText{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_TEXT_UID")
    private Long displayTextUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(name = "DISPLAY_TEXT_DETAIL", nullable = false)
    private String displayTextDetail;

    @Column(name = "DISPLAY_TEXT_COLOR", nullable = false)
    private String displayTextColor;

    @Column(name = "DISPLAY_TEXT_FONT", nullable = false)
    private String displayTextFont;

    @Column(name = "DISPLAY_TEXT_ROTATION", nullable = false)
    private Float displayTextRotation;

    @Column(name = "DISPLAY_TEXT_SCALE", nullable = false)
    private Float displayTextScale;

    @Column(name = "DISPLAY_TEXT_OFFSETX", nullable = false)
    private Float displayTextOffsetx;

    @Column(name = "DISPLAY_TEXT_OFFSETY", nullable = false)
    private Float displayTextOffsety;

    @Builder.Default
    @Column(name = "DISPLAY_TEXT_CREATED_AT", nullable = false)
    private LocalDateTime displayTextCreatedAt = LocalDateTime.now();
}