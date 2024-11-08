package com.d209.welight.domain.display.entity.displaylike;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_LIKE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(DisplayLikeId.class)
public class DisplayLike {

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_UID", nullable = false)
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID", nullable = false)
    private Display display;


    @Column(name = "LIKE_CREATED_AT")
    private LocalDateTime likeCreatedAt;
}