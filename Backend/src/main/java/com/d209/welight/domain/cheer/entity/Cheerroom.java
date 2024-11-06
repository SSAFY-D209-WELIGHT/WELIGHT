package com.d209.welight.domain.cheer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.d209.welight.domain.cheer.entity.cheerparticipation.CheerParticipation;
import com.d209.welight.domain.cheer.entity.cheerroomdisplay.CheerroomDisplay;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

@Entity
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHEERROOM")
public class Cheerroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHEERROOM_UID")
    private Long id;

    @Column(name = "CHEERROOM_NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "CHEERROOM_LATITUDE")
    @DecimalMin(value = "-90.0", message = "위도는 -90도 보다 커야 합니다")
    @DecimalMax(value = "90.0", message = "위도는 90도 보다 작아야 합니다")
    private Double latitude;

    @Column(name = "CHEERROOM_LONGITUDE")
    @DecimalMin(value = "-180.0", message = "경도는 -180도 보다 커야 합니다")
    @DecimalMax(value = "180.0", message = "경도는 180도 보다 작아야 합니다")
    private Double longitude;

    @Column(name = "CHEERROOM_IS_DONE", nullable = false)
    private boolean isDone;

    @Column(name = "CHEERROOM_CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "cheerroom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CheerParticipation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "cheerroom")
    private List<CheerroomDisplay> displays;  // 응원방에서 사용된 디스플레이 목록
}