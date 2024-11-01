package com.d209.welight.domain.display.controller;

import com.d209.welight.domain.display.dto.response.DisplayListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;

import com.d209.welight.domain.display.service.DisplayService;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.type.SortType;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/display")
@RequiredArgsConstructor
@Tag(name = "디스플레이 컨트롤러", description = "디스플레이 관련 기능 수행")
public class DisplayController {

    private final DisplayService displayService;

    @PostMapping
    @Operation(summary = "디스플레이 생성", description = "디스플레이 생성")
    public ResponseEntity<DisplayCreateResponse> createDisplay(@Valid @RequestBody DisplayCreateRequest request) {
        try {
            DisplayCreateResponse response = displayService.createDisplay(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            // 필요한 엔티티를 찾을 수 없는 경우 (예: 연관된 사용자나 리소스가 없는 경우)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // 잘못된 입력값이 들어온 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            // 기타 예상치 못한 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{displayId}")
    @Operation(summary = "디스플레이 상세 조회", description = "디스플레이 상세 정보를 조회합니다.")
    public ResponseEntity<DisplayDetailResponse> getDisplayDetail(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userDetails.getUsername(); // userId

            DisplayDetailRequest request = DisplayDetailRequest.builder()
                    .displayUid(displayId)
                    .userId(userId) // userUid 대신 userId로 변경
                    .build();

            DisplayDetailResponse response = displayService.getDisplayDetail(request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) { // 디스플레이를 찾을 수 없는 경우
            return ResponseEntity.notFound().build();
        } catch (Exception e) { // 예외 발생 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "전체 디스플레이 조회", description = "게시 여부 1인 디스플레이만 조회 (최신순, 좋아요순, 다운로드순)")
    public ResponseEntity<DisplayListResponse> getDisplayList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {

        Sort sort = switch (sortType) {
            case LATEST -> Sort.by("displayCreatedAt").descending()
                             .and(Sort.by("displayLikeCount").descending());
            case LIKES -> Sort.by("displayLikeCount").descending()
                             .and(Sort.by("displayCreatedAt").descending());
            case DOWNLOADS -> Sort.by("displayDownloadCount").descending()
                                 .and(Sort.by("displayCreatedAt").descending());
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        DisplayListResponse response = displayService.getDisplayList(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mylist")
    @Operation(summary = "내 디스플레이 목록 조회", description = "현재 사용자가 제작한 디스플레이 목록 조회")
    public ResponseEntity<DisplayListResponse> getMyDisplayList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") SortType sortType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Sort sort = switch (sortType) {
            case LATEST -> Sort.by("displayCreatedAt").descending()
                             .and(Sort.by("displayLikeCount").descending());
            case LIKES -> Sort.by("displayLikeCount").descending()
                             .and(Sort.by("displayCreatedAt").descending());
            case DOWNLOADS -> Sort.by("displayDownloadCount").descending()
                                 .and(Sort.by("displayCreatedAt").descending());
        };

        Pageable pageable = PageRequest.of(page, size, sort);
        String userId = userDetails.getUsername();
        DisplayListResponse response = displayService.getMyDisplayList(userId, pageable);
        return ResponseEntity.ok(response);
    }

}
