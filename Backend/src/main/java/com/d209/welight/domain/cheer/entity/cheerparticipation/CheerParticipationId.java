package com.d209.welight.domain.cheer.entity.cheerparticipation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CheerParticipationId implements Serializable {
    private Long user;      // CheerParticipation의 user 필드명과 동일
    private Long cheerroom; // CheerParticipation의 cheerroom 필드명과 동일
}