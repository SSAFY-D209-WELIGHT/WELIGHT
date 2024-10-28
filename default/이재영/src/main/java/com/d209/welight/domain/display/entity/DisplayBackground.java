package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DISPLAY_BACKGROUND")
public class DisplayBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_BACKGROUND_UID")
    private Long displayBackgroundUid;

    @Column(name = "DISPLAY_UID", nullable = false)
    private Long displayUid;

    @Column(name = "DISPLAY_BACKGROUND_INFO", nullable = false)
    private String displayBackgroundInfo;

    @Column(name = "DISPLAY_BACKGROUND_CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date displayBackgroundCreatedAt;
}