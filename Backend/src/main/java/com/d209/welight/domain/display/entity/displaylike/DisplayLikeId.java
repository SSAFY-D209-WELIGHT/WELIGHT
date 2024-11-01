package com.d209.welight.domain.display.entity.displaylike;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DisplayLikeId implements Serializable {
    private Long user;
    private Long display;
}