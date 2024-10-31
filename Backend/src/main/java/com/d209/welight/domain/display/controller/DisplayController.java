package com.d209.welight.domain.display.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

}
