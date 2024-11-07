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
        try {
            FindByGeoRequest geoDTO = new FindByGeoRequest(latitude, longitude, radius);
            List<CheerroomResponse> cheerRooms = cheerService.getAllCheerroomsByGeo(geoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(cheerRooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{cheerId}/delegate")
    @Operation(summary = "방장 위임")
    public ResponseEntity<?> delegateLeader(Authentication authentication,
                                                            @PathVariable(name="cheerId") long cheerId,
                                                            @RequestBody LeaderDelegateRequest leaderDelegateRequest) {
        try {
            User currentLeader = userService.findByUserId(authentication.getName());
            if (currentLeader == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }
            User newLeader = userService.findByUserUid(leaderDelegateRequest.getUserUid());
            if (newLeader == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            cheerService.delegateLeader(cheerId, currentLeader, newLeader);

            return ResponseEntity.ok().body("그룹 방장 위임 완료");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{cheerId}/participants")
    @Operation(summary = "해당 응원방에 참여 중인 유저 리스트")
    public ResponseEntity<?> getParticipants(Authentication authentication,
                                             @PathVariable(name="cheerId") long cheerId) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            List<ParticipantsResponse> participantsResponseList = cheerService.getParticipants(cheerId);

            return ResponseEntity.ok().body(participantsResponseList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{cheerId}/end")
    @Operation(summary = "응원 종료 (방장)")
    public ResponseEntity<?> endCheering(Authentication authentication,
                                            @PathVariable(name="cheerId") long cheerId) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            cheerService.endCheering(user, cheerId);

            return ResponseEntity.ok().body("응원 종료 성공");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /* 기록 */
    @PostMapping("{cheerId}/records")
    @Operation(summary = "응원 기록 생성")
    public ResponseEntity<?> createRecords(Authentication authentication,
                                           @PathVariable(name="cheerId") long cheerId,
                                           @RequestBody CheerRecordRequest cheerRecordRequest) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            cheerService.createRecords(user, cheerId, cheerRecordRequest);

            return ResponseEntity.ok().body("응원 기록 생성 완료");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("{cheerId}/records")
    @Operation(summary = "응원 기록 삭제")
    public ResponseEntity<?> createRecords(Authentication authentication,
                                           @PathVariable(name="cheerId") long cheerId) {
        try {
            User user = userService.findByUserId(authentication.getName());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
            }

            cheerService.deleteRecords(user, cheerId);

            return ResponseEntity.ok().body("응원 기록 삭제 완료");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{cheerId}/enter")
    @Operation(summary = "응원방 입장", description = "응원방에 입장합니다.")
    public ResponseEntity<String> enterCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cheerId) {
        cheerService.enterCheerroom(userDetails.getUsername(), cheerId);
        return ResponseEntity.ok(String.format("응원방 '%d'에 사용자 '%s'가 입장합니다.",
                cheerId, userDetails.getUsername()));
    }

    @PatchMapping("/{cheerId}/leave")
    @Operation(summary = "응원방 나가기", description = "응원방에서 나갑니다.")
    public ResponseEntity<?> leaveCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cheerId) {
        cheerService.leaveCheerroom(userDetails.getUsername(), cheerId);
        return ResponseEntity.ok(String.format("응원방 '%d'에 사용자 '%s'가 퇴장합니다.",
                cheerId, userDetails.getUsername()));
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

}
