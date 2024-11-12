package com.d209.welight.domain.display.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DISPLAY_TAG")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPLAY_TAG_UID")
    private Long tagUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;

    @Column(name = "DISPLAY_TAG_TEXT",nullable = false)
    private String displayTagText;

    @Column(name = "DISPLAY_TAG_CREATED_AT", nullable = false)
    private LocalDateTime displayTagCreatedAt;

}