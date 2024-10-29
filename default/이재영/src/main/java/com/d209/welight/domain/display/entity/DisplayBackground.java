package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_BACKGROUND")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayBackgroundUid;

    @OneToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @Column(nullable = false)
    private Float displayBackgroundBrightness;

    @Column(nullable = false)
    private LocalDateTime displayBackgroundCreatedAt = LocalDateTime.now();
}