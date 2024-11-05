package com.d209.welight.domain.cheer.entity;

import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHEER_PARTICIPATION")
public class CheerParticipation {
    @EmbeddedId
    private CheerParticipationId id;

    @MapsId("userUid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UID")
    private User user;

    @MapsId("cheerroomUid")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHEERROOM_UID")
    private Cheerroom cheerroom;

    @Column(name = "CHEER_PARTICIPATION_DATE", nullable = false)
    private LocalDateTime participationDate;

    @Column(name = "CHEERROOM_IS_OWNER", nullable = false)
    private boolean isOwner;

    @Column(name = "CHEER_MEMO")
    private String memo;
}