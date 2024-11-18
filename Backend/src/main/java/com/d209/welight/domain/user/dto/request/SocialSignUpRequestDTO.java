package com.d209.welight.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Builder
public class SocialSignUpRequestDTO {
    @Schema(description = "회원 소셜 아이디", example = "1234123412341234567")
    private String userId;
    @Schema(description = "회원 구글 프로필 이름", example = "이재영")
    private String userNickname;
    @Schema(description = "회원 프로필 사진", example = "구글에서 받아온 이미지 url")
    private String userProfileImg;
    @Schema(description = "회원 로그인처", example = "Google")
    private String userLogin;
}

