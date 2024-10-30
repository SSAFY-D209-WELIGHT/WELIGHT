package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_TEXT")
@Data

@NoArgsConstructor
@AllArgsConstructor
public class DisplayText {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayTextUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(nullable = false)
    private String displayTextDetail;

    @Column(nullable = false)
    private String displayTextColor;

    @Column(nullable = false)
    private String displayTextFont;

    @Column(nullable = false)
    private Float displayTextRotation;

    @Column(nullable = false)
    private String displayTextPosition;

    @Column(nullable = false)
    private LocalDateTime displayTextCreatedAt = LocalDateTime.now();
}