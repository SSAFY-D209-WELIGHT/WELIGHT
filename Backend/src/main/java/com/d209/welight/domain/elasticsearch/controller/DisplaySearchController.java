package com.d209.welight.domain.elasticsearch.controller;

import com.d209.welight.domain.elasticsearch.document.DisplayDocument;
import com.d209.welight.domain.elasticsearch.service.DisplaySearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/elasticsearch")
@Tag(name = "디스플레이 게시판 검색", description = "엘라스틱서치 활용 검색 기능 제공")
@RequiredArgsConstructor
@Slf4j
public class DisplaySearchController {
    private final DisplaySearchService searchService;

    @GetMapping
    @Operation(summary = "디스플레이 검색", description = "사용자 아이디, 디스플레이 이름, 태그를 활용하여 검색합니다.")
    public ResponseEntity<Page<DisplayDocument>> searchDisplays(
            @RequestParam(required = false, defaultValue = "") String userNickname,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(searchService.search(userNickname, keyword, pageable));
    }

}
