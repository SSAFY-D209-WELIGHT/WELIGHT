package com.d209.welight.domain.cheer.controller;


import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.request.LeaderDelegateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.dto.response.ParticipantsResponse;
import com.d209.welight.domain.cheer.service.CheerService;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheer")
public class CheerController {

    private final UserService userService;
    private final CheerService cheerService;

    @PostMapping("/room")
    public ResponseEntity<CheerroomResponse> createCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheerroomCreateRequest request) {
        try {
            CheerroomResponse response = cheerService.createCheerroom(userDetails.getUsername(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 위치 정보입니다.", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "응원방 생성 중 오류가 발생했습니다.", e);
        }
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
}
