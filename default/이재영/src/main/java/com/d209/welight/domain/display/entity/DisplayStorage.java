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
@Table(name = "DISPLAY_STORAGE")

public class DisplayStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Add a primary key column if needed

    @Column(name = "USER_UID", nullable = false)
    private Long userUid;

    @Column(name = "DISPLAY_UID", nullable = false)
    private Long displayUid;

    @Column(name = "DOWNLOAD_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date downloadAt;

    @Column(name = "IS_FAVORITES", nullable = false)
    private Boolean isFavorites;

    @Column(name = "FAVORITES_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date favoritesAt;
}