package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DISPLAY_COLOR")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayColor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayColorUid;

    @OneToOne
    @JoinColumn(name = "DISPLAY_BACKGROUND_UID", nullable = false)
    private DisplayBackground displayBackground;

    @Column(length = 7)
    private String displayColorSolid;

    @Column(length = 7)
    private String displayBackgroundGradationColor1;

    @Column(length = 7)
    private String displayBackgroundGradationColor2;

    @Column(length = 50)
    private String displayBackgroundGradationType;
}