package com.d209.welight.domain.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "USER")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_UID", nullable = false)
    @Schema(description = "회원 고유 번호", example = "1")
    private long userUid;

    @Column(name = "USER_ID", nullable = false)
    @Schema(description = "회원 아이디", example = "abcde")
    private String userId;

    @Column(name = "USER_PASSWORD", nullable = false)
    @Schema(description = "회원 비밀번호", example = "암호화된 내용")
    private String userPassword;

    @Column(name = "USER_NICKNAME", nullable = false)
    @Schema(description = "회원 닉네임", example = "TEST")
    private String userNickname;

    @Column(name = "USER_PROFILE_IMG")
    @Schema(description = "회원 프로필 사진", example = "multipart 이미지")
    private String userProfileImg;

    @Column(name = "USER_LOGIN", nullable = false)
    @Schema(description = "회원 로그인 유형", example = "Form")
    private String userLogin;

    @Column(name = "USER_IS_ADMIN", nullable = false)
    @Schema(description = "회원 관리자 유무", example = "false")
    private boolean userIsAdmin;

//    @Column(name = "user_refreshtoken", nullable = false)
//    @Schema(description = "회원 refreshToken", example = "ex7534487435468~~")
//    private String userRefreshToken;

    @Column(name = "USER_SIGNUP_DATE")
    @Schema(description = "회원 가입 일자", example = "2024.09.12 14:00:00")
    private LocalDateTime userSignupDate;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userIsAdmin ? "ROLE_ADMIN" : "ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return userPassword;
    }

    @Override
    public String getUsername() {
        return userNickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
