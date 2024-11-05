package com.d209.welight.domain.cheer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CheerParticipationId implements Serializable {
    @Column(name = "USER_UID")
    private Long userUid;

    @Column(name = "CHEERROOM_UID")
    private Long cheerroomUid;
}