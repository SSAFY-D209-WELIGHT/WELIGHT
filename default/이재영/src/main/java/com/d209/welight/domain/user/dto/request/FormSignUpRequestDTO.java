package com.d209.welight.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class FormSignUpRequestDTO {
    @Schema(description = "회원 아이디", example = "1")
    private String userId;
    @Schema(description = "회원 PW", example = "1234")
    private String userPassword;
    @Schema(description = "회원 닉네임", example = "TEST")
    private String userNickname;
    @Schema(description = "회원 관리자 유무", example = "false")
    private boolean userIsAdmin;
}
