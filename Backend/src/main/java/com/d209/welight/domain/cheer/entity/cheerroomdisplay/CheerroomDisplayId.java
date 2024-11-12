package com.d209.welight.domain.cheer.entity.cheerroomdisplay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CheerroomDisplayId implements Serializable {
    private Long cheerroom;  // CheerroomDisplay의 cheerroom 필드명과 동일해야 함
    private Long display;    // CheerroomDisplay의 display 필드명과 동일해야 함
} 