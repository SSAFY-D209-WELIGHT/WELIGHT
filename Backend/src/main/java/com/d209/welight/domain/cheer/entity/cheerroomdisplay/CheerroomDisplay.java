package com.d209.welight.domain.cheer.entity.cheerroomdisplay;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.display.entity.Display;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(CheerroomDisplayId.class)
public class CheerroomDisplay {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHEERROOM_UID")
    private Cheerroom cheerroom;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPLAY_UID")
    private Display display;
} 