package com.d209.welight.domain.display.entity.displaystorage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DisplayStorageId implements Serializable {
    private Long user;
    private Long display;
}