package com.d209.welight.domain.display.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayListResponse implements Serializable {
    private int currentPage;
    private List<DisplayInfo> displays;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayInfo implements Serializable {
        private Long displayUid;
        private String displayThumbnail;
        private boolean isFavorite; // 즐겨찾기 여부
    }
}
