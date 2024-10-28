package com.d209.welight.domain.display.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "DISPLAY_LIKE")
public class DisplayLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Add a primary key column if needed

    @Column(name = "DISPLAY_UID", nullable = false)
    private Long displayUid;

    @Column(name = "USER_UID", nullable = false)
    private Long userUid;

    @Column(name = "LIKE_CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date likeCreatedAt;

}