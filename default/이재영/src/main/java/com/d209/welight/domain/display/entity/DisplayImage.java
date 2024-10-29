package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_IMAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long displayImgUid;

    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @Column(nullable = false)
    private String displayImgUrl;

    @Column(nullable = false)
    private LocalDateTime displayImgCreatedAt = LocalDateTime.now();
}