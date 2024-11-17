package com.d209.welight.domain.cheer.controller;

import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.request.LeaderDelegateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryDetailResponse;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryResponse;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.dto.response.ParticipantsResponse;
import com.d209.welight.domain.cheer.service.CheerService;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheer")
@Tag(name = "응원 컨트롤러", description = "응원 관련 기능 수행")
public class CheerController {

    private final UserService userService;
    private final CheerService cheerService;

    @PostMapping
    @Operation(summary = "응원방 생성", description = "응원방을 생성합니다.")
    public ResponseEntity<CheerroomResponse> createCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheerroomCreateRequest request) {
        CheerroomResponse response = cheerService.createCheerroom(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "내 위치 기반 n KM 반경 내의 응원방 리스트 조회")
    public ResponseEntity<List<CheerroomResponse>> getAllCheerroomsByGeo(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        FindByGeoRequest geoDTO = new FindByGeoRequest(latitude, longitude, radius);
        List<CheerroomResponse> cheerRooms = cheerService.getAllCheerroomsByGeo(geoDTO);
        return ResponseEntity.ok(cheerRooms);
    }

    @PatchMapping("/{cheerId}/delegate")
    @Operation(summary = "방장 위임")
    public ResponseEntity<String> delegateLeader(Authentication authentication,
                                                            @PathVariable(name="cheerId") long cheerId,
                                                            @RequestBody LeaderDelegateRequest leaderDelegateRequest) {
        User currentLeader = userService.findByUserId(authentication.getName());
        User newLeader = userService.findByUserUid(leaderDelegateRequest.getUserUid());
        cheerService.delegateLeader(cheerId, currentLeader, newLeader);
        return ResponseEntity.ok("그룹 방장 위임 완료");
    }

    @GetMapping("/{cheerId}/participants")
    @Operation(summary = "해당 응원방에 참여 중인 유저 리스트")
    public ResponseEntity<List<ParticipantsResponse>> getParticipants(Authentication authentication,
                                             @PathVariable(name="cheerId") long cheerId) {
        User user = userService.findByUserId(authentication.getName());
        List<ParticipantsResponse> participantsResponseList = cheerService.getParticipants(cheerId);
        return ResponseEntity.ok().body(participantsResponseList);
    }

    @PatchMapping("/{cheerNumber}/end")
    @Operation(summary = "응원 종료 (방장)")
    public ResponseEntity<String> endCheering(Authentication authentication,
                                            @PathVariable(name="cheerNumber") long cheerNumber) {
        User user = userService.findByUserId(authentication.getName());
        cheerService.endCheering(user, cheerNumber);
        return ResponseEntity.ok("응원 종료 성공");
    }

    /* 기록 */
    @PostMapping("{cheerId}/records")
    @Operation(summary = "응원 기록 생성")
    public ResponseEntity<String> createRecords(Authentication authentication,
                                           @PathVariable(name="cheerId") long cheerId,
                                           @RequestBody CheerRecordRequest cheerRecordRequest) {
        User user = userService.findByUserId(authentication.getName());
        cheerService.createRecords(user, cheerId, cheerRecordRequest);
        return ResponseEntity.ok("응원 기록 생성 완료");
    }

    @DeleteMapping("{cheerId}/records")
    @Operation(summary = "응원 기록 삭제")
    public ResponseEntity<String> createRecords(Authentication authentication,
                                           @PathVariable(name="cheerId") long cheerId) {
        User user = userService.findByUserId(authentication.getName());
        cheerService.deleteRecords(user, cheerId);
        return ResponseEntity.ok("응원 기록 삭제 완료");
    }

    @PostMapping("/{cheerNumber}/enter")
    @Operation(summary = "응원방 입장", description = "응원방에 입장합니다.")
    public ResponseEntity<String> enterCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cheerNumber) {
        cheerService.enterCheerroom(userDetails.getUsername(), cheerNumber);
        return ResponseEntity.ok(String.format("응원방 '%d'에 사용자 '%s'가 입장합니다.",
                cheerNumber, userDetails.getUsername()));
    }

    @PatchMapping("/{cheerNumber}/leave")
    @Operation(summary = "응원방 나가기", description = "응원방에서 나갑니다.")
    public ResponseEntity<String> leaveCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cheerNumber) {
        cheerService.leaveCheerroom(userDetails.getUsername(), cheerNumber);
        return ResponseEntity.ok(String.format("응원방 '%d'에 사용자 '%s'가 퇴장합니다.",
                cheerNumber, userDetails.getUsername()));
    }

    @GetMapping("/records")
    @Operation(summary = "사용자의 응원 기록 목록 조회", description = "사용자의 응원 기록을 조회합니다." )
    public ResponseEntity<List<CheerHistoryResponse>> getMyCheerHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        List<CheerHistoryResponse> histories = cheerService.getUserCheerHistory(userId);
        return ResponseEntity.ok(histories);
    }


    @GetMapping("/{cheerId}/records")
    @Operation(summary = "응원 기록 상세 조회", description = "해당 응원 기록에 대한 상세 정보를 조회합니다.")
    public ResponseEntity<CheerHistoryDetailResponse> getCheerHistoryDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cheerId
    ) {
        String userId = userDetails.getUsername();
        CheerHistoryDetailResponse detail = cheerService.getCheerHistoryDetail(userId, cheerId);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{cheerId}/{displayId}")
    @Operation(summary = "응원방 디스플레이 설정 (방장)", description = "응원방에 사용할 디스플레이를 설정합니다.")
    public ResponseEntity<?> updateCheerroomDisplay(
            @PathVariable Long cheerId,
            @PathVariable Long displayId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userId = userDetails.getUsername();
        CheerHistoryResponse response = cheerService.useDisplayForCheer(cheerId, userId, displayId);
        return ResponseEntity.ok(response);
    }

}
