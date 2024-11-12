package com.d209.welight.domain.display.entity.displaystorage;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_STORAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(DisplayStorageId.class)
public class DisplayStorage {

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_UID", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @Column(name = "DOWNLOAD_AT", nullable = false)
    private LocalDateTime downloadAt;

    @Column(name = "IS_FAVORITES", nullable = false)
    private Boolean isFavorites;

    @Column(name = "FAVORITES_AT")
    private LocalDateTime favoritesAt;
}