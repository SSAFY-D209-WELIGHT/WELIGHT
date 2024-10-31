package com.d209.welight.domain.display.entity;

import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_STORAGE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_UID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;

    @Column(nullable = false)
    private LocalDateTime downloadAt;

    @Column(nullable = false)
    private Boolean isFavorites;

    private LocalDateTime favoritesAt;
}