package com.d209.welight.domain.display.controller;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityExistsException;
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

import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/display")
@RequiredArgsConstructor
@Tag(name = "디스플레이 컨트롤러", description = "디스플레이 관련 기능 수행")
public class DisplayController {

    private final UserService userService;
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

    @GetMapping("/{displayUid}")
    @Operation(summary = "디스플레이 상세 조회", description = "디스플레이 상세 정보를 조회합니다.")
    public ResponseEntity<DisplayDetailResponse> getDisplayDetail(
            @PathVariable Long displayUid,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String userId = userDetails.getUsername(); // userId로 변경

            DisplayDetailRequest request = DisplayDetailRequest.builder()
                    .displayUid(displayUid)
                    .userId(Long.parseLong(userId)) // userUid 대신 userId로 변경
                    .build();

            DisplayDetailResponse response = displayService.getDisplayDetail(request);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) { // 디스플레이를 찾을 수 없는 경우
            return ResponseEntity.notFound().build();
        } catch (Exception e) { // 예외 발생 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
}
