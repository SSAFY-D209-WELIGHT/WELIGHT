package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private Long displayImgUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(nullable = false)
    private String displayImgUrl;

    @Column(nullable = false)
    private String displayImgPosition;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime displayImgCreatedAt = LocalDateTime.now();
}