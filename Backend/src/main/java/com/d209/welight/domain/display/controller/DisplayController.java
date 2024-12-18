package com.d209.welight.domain.display.controller;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.response.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.d209.welight.domain.display.service.DisplayService;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.type.SortType;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/display")
@RequiredArgsConstructor
@Tag(name = "디스플레이 컨트롤러", description = "디스플레이 관련 기능 수행")
@Slf4j
public class DisplayController {

    private final DisplayService displayService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "디스플레이 생성", description = "디스플레이를 생성합니다.")
    public ResponseEntity<DisplayCreateResponse> createDisplay(@AuthenticationPrincipal UserDetails userDetails,
                                                               @Valid @RequestBody DisplayCreateRequest request) {
        String userId = userDetails.getUsername();
        DisplayCreateResponse response = displayService.createDisplay(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{displayId}")
    @Operation(summary = "디스플레이 정보 조회", description = "디스플레이 정보를 조회합니다.")
    public ResponseEntity<DisplayDetailResponse> getDisplayDetail(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();

        DisplayDetailRequest request = DisplayDetailRequest.builder()
                .displayUid(displayId)
                .userId(userId)
                .build();

        DisplayDetailResponse response = displayService.getDisplayDetail(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "전체 디스플레이 조회", description = "게시 여부 1인 디스플레이만 조회합니다. (최신순, 좋아요순, 다운로드순)")
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
    @Operation(summary = "내 디스플레이 목록 조회", description = "사용자가 제작한 디스플레이와 저장한 디스플레이를 조회합니다.")

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

    @PostMapping("/{displayId}/duplicate")
    @Operation(summary = "디스플레이 복제", description = "선택한 디스플레이를 복제합니다.")
    public ResponseEntity<?> duplicateDisplay(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        DisplayCreateResponse response = displayService.duplicateDisplay(displayId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{displayId}/edit")
    @Operation(summary = "디스플레이 수정 정보 조회", description = "수정할 디스플레이의 정보를 조회합니다.")
    public ResponseEntity<?> getDisplayForEdit(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DisplayCreateRequest response = displayService.getDisplayForEdit(displayId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{displayId}/edit")
    @Operation(summary = "디스플레이 수정", description = "수정된 디스플레이를 생성합니다.")
    public ResponseEntity<?> updateDisplay(
            @PathVariable Long displayId,
            @RequestBody DisplayCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DisplayCreateResponse response = displayService.updateDisplay(displayId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{displayId}")
    @Operation(summary = "디스플레이 삭제", description = "특정 디스플레이를 삭제합니다.")
    public ResponseEntity<?> deleteDisplay(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        displayService.deleteDisplay(displayId, userDetails.getUsername());
        return ResponseEntity.ok().body("디스플레이가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/{displayId}/storage")
    @Operation(summary = "디스플레이 다운로드", description = "디스플레이를 저장소에 저장합니다.")
    public ResponseEntity<?> downloadDisplay( @AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable("displayId") long displayUid) {
        DisplayCreateResponse response = displayService.downloadDisplay(userDetails.getUsername(), displayUid);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{displayId}/storage")
    @Operation(summary = "저장된 디스플레이 삭제", description = "사용자의 저장소에서 디스플레이를 삭제합니다.")
    public ResponseEntity<?> deleteStoredDisplay(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable("displayId") long displayUid) {
        DisplayCreateResponse response = displayService.deleteStoredDisplay(userDetails.getUsername(), displayUid);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{displayId}/favorite")
    @Operation(summary = "디스플레이 즐겨찾기 토글", description = "디스플레이의 즐겨찾기 상태를 변경합니다.")
    public ResponseEntity<?> updateDisplayFavorite(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable("displayId") long displayUid){
        DisplayCreateResponse response = displayService.updateDisplayFavorite(userDetails.getUsername(), displayUid);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/like")
    @Operation(summary = "디스플레이 좋아요 목록", description = "좋아요를 누른 디스플레이 목록 보기")
    public ResponseEntity getLikedDisplayList(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        DisplayLikedListResponse response = displayService.getLikedDisplayList(userDetails.getUsername(), pageable);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/{displayId}/like")
    @Operation(summary = "디스플레이 좋아요", description = "디스플레이 좋아요 기능")
    public ResponseEntity<?> doLikeDisplay(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable("displayId") long displayUid){
        displayService.doLikeDisplay(userDetails.getUsername(), displayUid);

        Map<String, String> response = new HashMap<>();
        response.put("message", "디스플레이 좋아요 완료");

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{displayId}/like")
    @Operation(summary = "디스플레이 좋아요 취소", description = "디스플레이 좋아요 취소 기능")
    public ResponseEntity<?> cancelLikeDisplay(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable("displayId") long displayUid) {
        displayService.cancelLikeDisplay(userDetails.getUsername(), displayUid);

        Map<String, String> response = new HashMap<>();
        response.put("message", "디스플레이 좋아요 취소");

        return ResponseEntity.ok().body(response);
    }

    /*
    * 댓글 (조회, 생성, 수정, 삭제)
    * */
    @GetMapping("/{displayId}/comment")
    @Operation(summary = "댓글 목록 조회", description = "디스플레이의 모든 댓글을 조회합니다.")
    public ResponseEntity<?> getComments(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable("displayId") long displayUid) {
        return ResponseEntity.ok(displayService.getComments(userDetails.getUsername(), displayUid));
    }

    @PostMapping("/{displayId}/comment")
    @Operation(summary = "댓글 작성", description = "디스플레이에 새 댓글을 작성합니다.")
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("displayId") Long displayId,
            @RequestBody @Valid DisplayCommentRequest requestDTO) {

        displayService.createComment(userDetails.getUsername(), displayId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 생성 완료");
    }

    @PatchMapping("/{displayId}/comment")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("displayId") Long displayId,
            @RequestBody @Valid DisplayCommentUpdateRequest requestDTO) {

        displayService.updateComment(userDetails.getUsername(), displayId, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 수정 완료");
    }

    @DeleteMapping("/{displayId}/comment/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("displayId") Long displayId,
            @PathVariable("commentId") Long commentId) {

        displayService.deleteComment(userDetails.getUsername(), displayId, commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body("댓글 삭제 완료");
    }

    @PatchMapping("/{displayId}/isposted")
    @Operation(summary = "디스플레이 게시 여부 토글", description = "디스플레이 게시 여부를 토글합니다.")
    public ResponseEntity<?> toggleDisplayStatus(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        DisplayPostedToggleResponse response = displayService.updateDisplayStatus(displayId, userId);
        return ResponseEntity.ok(response);
    }
}

