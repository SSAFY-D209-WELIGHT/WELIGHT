package com.d209.welight.domain.user.controller;


import com.d209.welight.domain.user.dto.request.FormLoginRequestDTO;
import com.d209.welight.domain.user.dto.request.FormSignUpRequestDTO;
import com.d209.welight.domain.user.dto.request.PasswordChangeRequestDTO;
import com.d209.welight.domain.user.dto.request.SocialLoginRequestDTO;
import com.d209.welight.domain.user.dto.response.UserInfoResponseDTO;
import com.d209.welight.domain.user.service.CustomUserDetailsService;
import com.d209.welight.domain.user.service.UserService;
import com.d209.welight.global.util.jwt.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "회원 컨트롤러", description = "회원관련 기능 수행")
public class UserController {

    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원가입", description = "Form 회원가입")
    public ResponseEntity<?> signUp(
            @RequestPart("formSignUpRequestDTO") FormSignUpRequestDTO formSignUpRequestDTO,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        userDetailsService.createUser(formSignUpRequestDTO, image);
        // 200, 클라이언트 요청 성공
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/signup/{userId}")
    @Operation(summary = "아이디 중복 체크", description = "회원가입 시 아이디 중복 체크(소셜 로그인은 중복 위험 미존재)")
    public ResponseEntity<?> checkNickname(@PathVariable("userId") String userId) {
        boolean chk = userService.chkuserId(userId);
        // 닉네임 중복이 발생하지 않음
        return ResponseEntity.ok(chk);
    }

    @PostMapping("/login/form")
    @Operation(summary = "폼 로그인", description = "폼 로그인")
    public ResponseEntity<?> login(@RequestBody FormLoginRequestDTO request) {
        // 로그인 로직
        log.info("로그인 성공 - userId: {}", request.getUserId());
        JwtToken tokens = userService.FormLogin(request);
        // 200, 클라이언트 요청 성공
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login/social")
    @Operation(summary = "소셜 로그인", description = "소셜 로그인")
    public ResponseEntity<?> login(@RequestBody SocialLoginRequestDTO request) {
        JwtToken tokens = userService.SocialLogin(request);
        // 200, 클라이언트 요청 성공
        return ResponseEntity.ok(tokens);
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 조회", description = "액세스 토큰을 사용해 회원 정보 조회")
    public ResponseEntity<?> info(Authentication  authentication) {
        UserInfoResponseDTO response = userService.info(authentication.getName());
        // 200, 클라이언트 요청 성공
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회원 이미지 수정", description = "마이 페이지에서 회원 이미지 수정")
    public ResponseEntity<?> updateImg
            (Authentication authentication, @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        return ResponseEntity.ok(userService.updateImg(authentication.getName(), image));
    }

    @DeleteMapping("/img")
    @Operation(summary = "회원 이미지 삭제", description = "마이 페이지에서 회원 이미지 삭제")
    public ResponseEntity<?> deleteImg
            (Authentication authentication) throws Exception {
        userService.deleteImg(authentication.getName());
        return ResponseEntity.ok("이미지 삭제 성공");
    }

    @PatchMapping("/nickname")
    @Operation(summary = "회원 닉네임 수정", description = "마이 페이지에서 회원 닉네임 수정")
    public ResponseEntity<?> updateNickname
            (Authentication authentication, @RequestParam String nickname) {
        userService.updateNickname(authentication.getName(), nickname);
        return ResponseEntity.ok("닉네임 수정 성공");
    }

    @PatchMapping("/password")
    @Operation(summary = "회원 비밀번호 수정", description = "마이 페이지에서 회원 비밀번호 수정")
    public ResponseEntity<?> updatePassword
            (Authentication authentication, @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {
        userDetailsService.changePassword(passwordChangeRequestDTO.getOldPassword(), passwordChangeRequestDTO.getNewPassword());
        return ResponseEntity.ok("비밀번호 수정 성공");
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 시행")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        userService.delete(authentication.getName());
        return ResponseEntity.ok("회원 탈퇴 성공");
    }
    @PostMapping("/jwt-token")
    @Operation(summary = "jwt 토큰 재발급", description = "유효기간 만료로 인한 JWT 토큰 재발급")
    public ResponseEntity<?> updateToken(@RequestParam String userRefreshToken) {
        JwtToken tokens = userService.updateToken(userRefreshToken);
        return ResponseEntity.ok(tokens);
    }
}

