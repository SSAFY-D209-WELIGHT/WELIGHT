package com.d209.welight.domain.display.controller;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.service.UserService;
import com.d209.welight.domain.display.dto.response.DisplayListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.d209.welight.domain.display.service.DisplayService;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.type.SortType;

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/display")
@RequiredArgsConstructor
@Tag(name = "디스플레이 컨트롤러", description = "디스플레이 관련 기능 수행")
@Slf4j
public class DisplayController {

    private final UserService userService;
    private final DisplayService displayService;

    @PostMapping
    @Operation(summary = "디스플레이 생성", description = "디스플레이를 생성합니다.")
    public ResponseEntity<DisplayCreateResponse> createDisplay(@Valid @RequestBody DisplayCreateRequest request) {
        try {
            DisplayCreateResponse response = displayService.createDisplay(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            log.error("디스플레이 생성 중 엔티티를 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            log.error("디스플레이 생성 중 잘못된 입력값: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("디스플레이 생성 중 예상치 못한 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{displayId}")
    @Operation(summary = "디스플레이 정보 조회", description = "디스플레이 정보를 조회합니다.")
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
    public ResponseEntity<String> duplicateDisplay(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            String userId = userDetails.getUsername();
            Long newDisplayId = displayService.duplicateDisplay(displayId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(String.format("디스플레이 복제에 성공했습니다!\n복제된 디스플레이 Uid: %d", newDisplayId));
        } catch (EntityNotFoundException e) {
            log.error("디스플레이 복제 중 엔티티를 찾을 수 없음: displayId={}, userId={}", displayId, userDetails.getUsername(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("디스플레이 복제 중 예상치 못한 오류 발생: displayId={}, userId={}", displayId, userDetails.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{displayId}/edit")
    @Operation(summary = "디스플레이 수정 정보 조회", description = "수정할 디스플레이의 정보를 조회합니다.")
    public ResponseEntity<?> getDisplayForEdit(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            DisplayCreateRequest response = displayService.getDisplayForEdit(displayId, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("디스플레이를 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("조회 권한이 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("디스플레이 정보 조회 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/{displayId}/edit")
    @Operation(summary = "디스플레이 수정", description = "수정된 디스플레이를 생성합니다.")
    public ResponseEntity<?> updateDisplay(
            @PathVariable Long displayId,
            @RequestBody DisplayCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            DisplayCreateResponse response = displayService.updateDisplay(displayId, request, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("디스플레이를 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("수정 권한이 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("디스플레이 수정 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{displayId}")
    @Operation(summary = "디스플레이 삭제", description = "특정 디스플레이를 삭제합니다.")
    public ResponseEntity<?> deleteDisplay(
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            displayService.deleteDisplay(displayId, userDetails.getUsername());
            return ResponseEntity.ok().body("디스플레이가 성공적으로 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("디스플레이를 찾을 수 없습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("디스플레이 삭제 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/{displayId}/storage")
    @Operation(summary = "디스플레이 다운로드", description = "디스플레이를 저장소에 저장합니다.")
    public ResponseEntity<?> downloadDisplay(Authentication authentication,
                                              @PathVariable("displayId") long displayUid) throws Exception{
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.downloadDisplay(user, displayUid);
            return ResponseEntity.ok().body("디스플레이 저장 완료");
        } catch (EntityNotFoundException e) { // 디스플레이를 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{displayId}/storage")
    @Operation(summary = "저장된 디스플레이 삭제", description = "사용자의 저장소에서 디스플레이를 삭제합니다.")
    public ResponseEntity<?> deleteStoredDisplay(Authentication authentication,
                                             @PathVariable("displayId") long displayUid) throws Exception {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.deleteStoredDisplay(user, displayUid);
            return ResponseEntity.ok().body("디스플레이가 저장소에서 삭제되었습니다.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{displayId}/favorite")
    @Operation(summary = "디스플레이 즐겨찾기 토글", description = "디스플레이의 즐겨찾기 상태를 변경합니다.")
    public ResponseEntity<?> updateDisplayFavorite(Authentication authentication,
                                             @PathVariable("displayId") long displayUid) throws Exception{
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.updateDisplayFavorite(user, displayUid);
            return ResponseEntity.ok().body("디스플레이 즐겨찾기 상태 변경 완료");
        } catch (EntityNotFoundException e) { // 디스플레이를 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{displayId}/like")
    @Operation(summary = "디스플레이 좋아요", description = "디스플레이 좋아요 기능")
    public ResponseEntity<?> doLikeDisplay(Authentication authentication,
                                             @PathVariable("displayId") long displayUid) throws Exception{
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.doLikeDisplay(user, displayUid);
            return ResponseEntity.ok().body("디스플레이 좋아요 완료");
        } catch (EntityNotFoundException e) { // 디스플레이를 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch(EntityExistsException e) { // 이미 좋아요 누른 경우
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{displayId}/like")
    @Operation(summary = "디스플레이 좋아요 취소", description = "디스플레이 좋아요 취소 기능")
    public ResponseEntity<?> cancelLikeDisplay(Authentication authentication,
                                                 @PathVariable("displayId") long displayUid) throws Exception {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.cancelLikeDisplay(user, displayUid);
            return ResponseEntity.ok().body("디스플레이 좋아요 취소");
        } catch (EntityNotFoundException e) { // 디스플레이 없음 , 좋아요 누른 적 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /*
    * 댓글 (조회, 생성, 수정, 삭제)
    * */
    @GetMapping("/{displayId}/comment")
    @Operation(summary = "댓글 목록 조회", description = "디스플레이의 모든 댓글을 조회합니다.")
    public ResponseEntity<?> getComments(Authentication authentication,
                                         @PathVariable("displayId") long displayUid) throws Exception {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            return ResponseEntity.ok(displayService.getComments(user, displayUid));

        } catch (EntityNotFoundException e) { // 디스플레이 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/{displayId}/comment")
    @Operation(summary = "댓글 작성", description = "디스플레이에 새 댓글을 작성합니다.")
    public ResponseEntity<?> createComment(
            Authentication authentication,
            @PathVariable("displayId") Long displayId,
            @RequestBody @Valid DisplayCommentRequest requestDTO) {

        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            displayService.createComment(user, displayId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글 생성 완료");
        } catch (EntityNotFoundException e) { // 디스플레이 없음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("/{displayId}/comment")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public ResponseEntity<?> updateComment(
            Authentication authentication,
            @PathVariable("displayId") Long displayId,
            @RequestBody @Valid DisplayCommentUpdateRequest requestDTO) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }
            displayService.updateComment(user, displayId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글 수정 완료");
        } catch (EntityNotFoundException e) {
            // 디스플레이나 댓글을 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // 디스플레이의 댓글이 아니거나, 자신의 댓글이 아닌 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/{displayId}/comment/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<?> deleteComment(
            Authentication authentication,
            @PathVariable("displayId") Long displayId,
            @PathVariable("commentId") Long commentId) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }
            displayService.deleteComment(user, displayId, commentId);
            return ResponseEntity.status(HttpStatus.CREATED).body("댓글 삭제 완료");
        } catch (EntityNotFoundException e) {
            // 디스플레이나 댓글을 찾을 수 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // 디스플레이의 댓글이 아니거나, 자신의 댓글이 아닌 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다.");
        }
    }
}

