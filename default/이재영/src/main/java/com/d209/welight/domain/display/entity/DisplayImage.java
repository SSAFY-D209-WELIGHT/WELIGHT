package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@Table(name = "DISPLAY_IMAGE")
@NoArgsConstructor
@AllArgsConstructor
public class DisplayImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_IMG_UID")
    private Long displayImgUid;

    @Column(name = "DISPLAY_UID", nullable = false)
    private Long displayUid;

    @Column(name = "DISPLAY_IMG_URL", nullable = false)
    private String displayImgUrl;

    @Column(name = "DISPLAY_IMG_CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date displayImgCreatedAt;
}