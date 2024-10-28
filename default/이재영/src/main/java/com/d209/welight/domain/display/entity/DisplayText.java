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
@Table(name = "DISPLAY_TEXT")
public class DisplayText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_TEXT_UID")
    private Long displayTextUid;

    @Column(name = "DISPLAY_UID", nullable = false)
    private Long displayUid;

    @Column(name = "DISPLAY_TEXT", nullable = false)
    private String displayText;

    @Column(name = "DISPLAY_TEXT_CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date displayTextCreatedAt;
}