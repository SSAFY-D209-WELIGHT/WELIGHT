package com.d209.welight.domain.display.entity;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
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

    @Column(nullable = false)
    private String displayTagText;

    @Column(nullable = false)
    private LocalDateTime displayTagCreatedAt;

}