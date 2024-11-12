package com.d209.welight.domain.display.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_BACKGROUND")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "display")
@EqualsAndHashCode(exclude = "display")
public class DisplayBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_BACKGROUND_UID", columnDefinition = "BIGINT")
    private Long displayBackgroundUid;

    @OneToOne
    @JoinColumn(name = "DISPLAY_UID")
    @JsonIgnore
    private Display display;

    @Column(name = "DISPLAY_BACKGROUND_BRIGHTNESS", nullable = false)
    private Float displayBackgroundBrightness;

    @Builder.Default
    @Column(name = "DISPLAY_BACKGROUND_CREATED_AT", nullable = false)
    private LocalDateTime displayBackgroundCreatedAt = LocalDateTime.now();

    // DisplayColor의 속성들을 DisplayBackground로 통합
    @Column(name = "DISPLAY_COLOR_SOLID", length = 9, nullable = true)
    private String displayColorSolid;

    @Column(name = "DISPLAY_BACKGROUND_GRADATION_COLOR1", length = 9, nullable = true)
    private String displayBackgroundGradationColor1;

    @Column(name = "DISPLAY_BACKGROUND_GRADATION_COLOR2", length = 9, nullable = true)
    private String displayBackgroundGradationColor2;

    @Column(name = "DISPLAY_BACKGROUND_GRADATION_TYPE", length = 50, nullable = true)
    private String displayBackgroundGradationType;
}