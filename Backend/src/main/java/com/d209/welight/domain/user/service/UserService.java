package com.d209.welight.domain.user.service;

import com.d209.welight.domain.user.dto.request.FormLoginRequestDTO;
import com.d209.welight.domain.user.dto.request.SocialLoginRequestDTO;
import com.d209.welight.domain.user.dto.response.UserInfoResponseDTO;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.global.exception.user.UserNotFoundException;
import com.d209.welight.global.util.jwt.JwtToken;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    // LoginRequestDTO를 사용해 폼 로그인 실행
    JwtToken FormLogin(FormLoginRequestDTO request);

    // LoginRequestDTO를 사용해 소셜 로그인 실행
    JwtToken SocialLogin(SocialLoginRequestDTO request);

    // JwtToken 재발행
    JwtToken updateToken(String userRefreshToken);

    // userId을 사용해 회원 정보 조회
    UserInfoResponseDTO info(String userId);

    // userProviderId을 사용해 회원 삭제
    void delete(String userId);

    // 회원 기존 프로필 이미지(userProfileImg)를 image로 변경
    String updateImg(String userName, MultipartFile image) throws Exception;

    // 회원 기존 프로필 이미지(userProfileImg)를 삭제
    void deleteImg(String userName) throws Exception;

    // 회원 기존 닉네임을 nickName으로 변경
    void updateNickname(String userName, String nickName);

    // userNickname을 닉네임 중복 체크
    boolean chkuserId(String userId);

    // userId로 사용자 찾기
    // @throws UserNotFoundException 사용자를 찾을 수 없는 경우
    User findByUserId(String userId);

    // userUid로 사용자 찾기
    // @throws UserNotFoundException 사용자를 찾을 수 없는 경우
    User findByUserUid(Long userUid);

}
