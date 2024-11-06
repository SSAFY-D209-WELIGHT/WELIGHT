package com.d209.welight.domain.cheer.controller;


import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.service.CheerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheer")
public class CheerController {

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
    public ResponseEntity<List<CheerroomResponse>> getAllCheerrooms(
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
}
