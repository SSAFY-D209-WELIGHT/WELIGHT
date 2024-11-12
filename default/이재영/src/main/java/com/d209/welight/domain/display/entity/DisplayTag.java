package com.d209.welight.domain.display.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "DISPLAY_TAG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(DisplayTagId.class)
public class DisplayTag {
    @Id
    @ManyToOne
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Id
    @ManyToOne
    @JoinColumn(name = "TAG_UID")
    private Tag tag;
}

@Data
class DisplayTagId implements Serializable {
    private Long display;
    private Long tag;
}