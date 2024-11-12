package com.d209.welight.domain.display.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayListResponse {
    private int currentPage;
    private List<DisplayInfo> displays;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisplayInfo {
        private Long displayUid;
        private String displayThumbnail;
        private boolean isFavorite; // 즐겨찾기 여부
    }
}
