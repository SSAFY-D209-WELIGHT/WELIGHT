package com.d209.welight.domain.display.entity;
import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_LIKE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayLike {
    @Id
    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Id
    @ManyToOne
    @JoinColumn(name = "USER_UID")
    private User user;

    @Column(nullable = false)
    private LocalDateTime likeCreatedAt = LocalDateTime.now();
}