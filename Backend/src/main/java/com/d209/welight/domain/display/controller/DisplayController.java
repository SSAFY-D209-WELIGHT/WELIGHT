package com.d209.welight.domain.display.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.d209.welight.domain.display.service.DisplayService;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/display")
@RequiredArgsConstructor
public class DisplayController {

    private final DisplayService displayService;

    @PostMapping
    @Operation(summary = "디스플레이 생성", description = "디스플레이 생성")
    public ResponseEntity<DisplayCreateResponse> createDisplay(@Valid @RequestBody DisplayCreateRequest request) {
        DisplayCreateResponse response = displayService.createDisplay(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
