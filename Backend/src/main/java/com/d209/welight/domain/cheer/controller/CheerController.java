package com.d209.welight.domain.cheer.controller;


import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.service.CheerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cheer")
@Tag(name = "응원 컨트롤러", description = "응원 관련 기능 수행")
public class CheerController {

    private final CheerService cheerService;

    @PostMapping
    @Operation(summary = "응원방 생성", description = "응원방을 생성합니다.")
    public ResponseEntity<CheerroomResponse> createCheerroom(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CheerroomCreateRequest request) {
        CheerroomResponse response = cheerService.createCheerroom(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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

}
