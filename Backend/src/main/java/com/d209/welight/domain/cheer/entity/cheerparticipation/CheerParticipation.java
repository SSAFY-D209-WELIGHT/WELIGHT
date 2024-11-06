package com.d209.welight.domain.cheer.entity.cheerparticipation;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CheerParticipationId.class)
public class CheerParticipation {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UID")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHEERROOM_UID")
    private Cheerroom cheerroom;

    @Column(name = "CHEER_PARTICIPATION_DATE", nullable = false)
    private LocalDateTime participationDate;

    @Column(name = "CHEERROOM_IS_OWNER", nullable = false)
    private boolean isOwner;

    @Column(name = "CHEER_MEMO")
    private String memo;

    @Column(name = "LAST_ENTRY_TIME")
    private LocalDateTime lastEntryTime;

    @Column(name = "LAST_EXIT_TIME")
    private LocalDateTime lastExitTime;

    @Column(name = "ENTRY_COUNT")
    @Builder.Default
    private Integer entryCount = 0;

    @Column(name = "TOTAL_DURATION")
    @Builder.Default
    private LocalTime totalDuration = LocalTime.of(0, 0, 0);

    public void updateCheerMemo(String cheerMemo) {
        this.memo = cheerMemo;
    }
}