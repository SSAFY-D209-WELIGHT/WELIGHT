package com.d209.welight.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Builder
public class SocialSignUpRequestDTO {
    @Schema(description = "회원 아이디", example = "1")
    private String userId;
    @Schema(description = "회원 닉네임", example = "TEST")
    private String userNickname;
    @Schema(description = "회원 프로필 사진", example = "String 이미지")
    private String userProfileImg;
    @Schema(description = "회원 로그인처", example = "Form")
    private String userLogin;
}

