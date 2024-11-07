package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_BACKGROUND")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_BACKGROUND_UID", columnDefinition = "BIGINT")
    private Long displayBackgroundUid;

    @OneToOne
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(name = "DISPLAY_BACKGROUND_BRIGHTNESS", nullable = false)
    private Float displayBackgroundBrightness;

    @Builder.Default
    @Column(name = "DISPLAY_BACKGROUND_CREATED_AT", nullable = false)
    private LocalDateTime displayBackgroundCreatedAt = LocalDateTime.now();
}