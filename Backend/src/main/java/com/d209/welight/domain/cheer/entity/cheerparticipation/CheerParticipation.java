package com.d209.welight.domain.cheer.entity.cheerparticipation;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.global.exception.cheer.CheerAccessDeniedException;
import com.d209.welight.global.exception.cheer.CheerNotFoundException;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CheerParticipationId.class)
@Table(name ="CHEER_PARTICIPATION")
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

    // 상태 변경 메소드들

    public void updateCheerMemo(String cheerMemo) {
        this.memo = cheerMemo;
    }

    public void delegateLeaderTo(CheerParticipation newLeader) {
        this.setOwner(false);
        newLeader.setOwner(true);
    }

    public void updateEntry() {
        this.lastEntryTime = LocalDateTime.now();
        this.lastExitTime = null;
        this.entryCount = this.entryCount + 1;
    }

    public LocalTime updateExitInfo(LocalDateTime exitTime) {
        this.setLastExitTime(exitTime);
        Duration cheerDuration = Duration.between(this.getLastEntryTime(), exitTime);
        LocalTime currentTotal = this.getTotalDuration();
        LocalTime newTotal = currentTotal.plusHours(cheerDuration.toHours())
                .plusMinutes(cheerDuration.toMinutesPart())
                .plusSeconds(cheerDuration.toSecondsPart());
        this.setTotalDuration(newTotal);
        return newTotal;
    }
    public static CheerParticipation createNewParticipation(User user, Cheerroom cheerroom, boolean isOwner) {
        return CheerParticipation.builder()
                .user(user)
                .cheerroom(cheerroom)
                .participationDate(LocalDateTime.now())
                .lastEntryTime(LocalDateTime.now())
                .isOwner(isOwner)
                .build();
    }

}