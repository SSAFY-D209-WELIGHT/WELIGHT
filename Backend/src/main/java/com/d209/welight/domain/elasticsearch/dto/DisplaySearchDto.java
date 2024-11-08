package com.d209.welight.domain.elasticsearch.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplaySearchDto {

    private String keyword;        // 검색어
    private String sortBy;         // 정렬 기준 (예: "name", "createdAt" 등)
    private String sortOrder;      // 정렬 순서 ("asc" 또는 "desc")

    @Builder.Default
    private int page = 0;          // 페이지 번호 (0부터 시작)

    @Builder.Default
    private int size = 10;         // 페이지당 항목 수

    // 기본 정렬 순서 반환
    public String getSortOrder() {
        return sortOrder != null ? sortOrder.toLowerCase() : "desc";
    }

    // 페이지 유효성 검사
    public int getPage() {
        return Math.max(0, page);
    }

    // 페이지 크기 유효성 검사
    public int getSize() {
        return Math.min(100, Math.max(1, size));  // 1 ~ 100 사이의 값으로 제한
    }
}