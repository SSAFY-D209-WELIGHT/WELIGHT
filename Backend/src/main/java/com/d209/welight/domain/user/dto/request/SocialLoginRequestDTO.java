package com.d209.welight.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SocialLoginRequestDTO {
    @Schema(description = "회원 아이디", example = "2")
    private String userId;
    @Schema(description = "회원 닉네임", example = "TEST")
    private String userNickname;
    @Schema(description = "회원 프로필 사진", example = "S3에 저장된 이미지 url")
    private String userProfileImg;
    @Schema(description = "회원 로그인처", example = "Form")
    private String userLogin;
}

