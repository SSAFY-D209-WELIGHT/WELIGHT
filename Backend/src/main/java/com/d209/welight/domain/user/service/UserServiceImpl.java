package com.d209.welight.domain.user.service;

import com.d209.welight.domain.user.dto.request.FormLoginRequestDTO;
import com.d209.welight.domain.user.dto.request.SocialLoginRequestDTO;
import com.d209.welight.domain.user.dto.request.SocialSignUpRequestDTO;
import com.d209.welight.domain.user.dto.response.UserInfoResponseDTO;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.global.exception.auth.InvalidTokenException;
import com.d209.welight.global.exception.common.NotFoundException;
import com.d209.welight.global.exception.user.UserNicknameDuplicateException;
import com.d209.welight.global.exception.user.UserNotFoundException;
import com.d209.welight.global.service.jwt.JwtTokenService;
import com.d209.welight.global.service.redis.RedisService;
//import com.d209.welight.global.service.s3.S3Service;
import com.d209.welight.global.service.s3.S3Service;
import com.d209.welight.global.util.jwt.JwtToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    // private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService userDetailsService;
    private final S3Service s3Service;
    private final RedisService redisService;
    private final UserRepository userRepository;

    @Override
    public JwtToken FormLogin(FormLoginRequestDTO request) {
        // 사용자 검증
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUserId());

        // 비밀번호 매칭 확인
        if (!passwordEncoder.matches(request.getUserPassword(), userDetails.getPassword()))
            throw new BadCredentialsException("유효하지 않은 비밀번호입니다.");

        // JWT 토큰 발급
        JwtToken token = jwtTokenService.generateToken(request.getUserId(), request.getUserPassword(), request.getUserLogin());
        redisService.saveRefreshToken(request.getUserId(), String.valueOf(token));
        return token;
    }

    @Override
    public JwtToken SocialLogin(SocialLoginRequestDTO request) {
        // 사용자 찾기
        User user = userRepository.findByUserId(request.getUserId())
                .orElse(null);

        // 사용자가 없는 경우
        if (user == null) {
            SocialSignUpRequestDTO requestDTO = SocialSignUpRequestDTO.builder()
                    .userId(request.getUserId())
                    .userNickname(request.getUserNickname())
                    .userProfileImg(request.getUserProfileImg())
                    .userLogin(request.getUserLogin())
                    .build();

            userDetailsService.createUser(requestDTO, request.getUserProfileImg());
        } else {
            // 비밀번호 매칭 확인
            if (!passwordEncoder.matches("", user.getUserPassword()))
                throw new BadCredentialsException("유효하지 않은 비밀번호입니다.");
        }

        // JWT 토큰 발급
        JwtToken token = jwtTokenService.generateToken(request.getUserId(), "", request.getUserLogin());
        redisService.saveRefreshToken(request.getUserId(), String.valueOf(token));
        return jwtTokenService.generateToken(request.getUserId(), "", request.getUserLogin());
    }

    @Override
    @Transactional
    public JwtToken updateToken(String userRefreshToken) {
        try {
            String userId = redisService.findUserIdByRefreshToken(userRefreshToken);
            if (userId == null) {
                throw new InvalidTokenException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
            }

            // 사용자 검증
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            // 토큰 확인
            String storedToken = redisService.getRefreshToken(userId);
            if (storedToken == null) {
                throw new InvalidTokenException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
            }

            if (!storedToken.contains(userRefreshToken)) {
                redisService.deleteRefreshToken(userId);
                throw new InvalidTokenException("유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요.");
            }

            // JWT 토큰 재발급
            JwtToken token = jwtTokenService.generateToken(userId, userDetails.getPassword(), null);
            redisService.saveRefreshToken(userId, String.valueOf(token));  // 전체 토큰 정보 저장
            return token;

        } catch (Exception e) {
            throw new InvalidTokenException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
        }
    }

    @Override
    public UserInfoResponseDTO info(String userId) {
        // 사용자 찾기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("해당 아이디에 맞는 회원을 찾을 수 없습니다."));

        // 필요한 정보만 UserInfoResponseDTO로 편집해 반환

        return UserInfoResponseDTO.builder()
                .userUid(user.getUserUid())
                .userNickname(user.getUserNickname())
                .userProfileImg(user.getUserProfileImg())
                .userLogin(user.getUserLogin())
                .userIsAdmin(user.isUserIsAdmin())
                .userSignupDate(user.getUserSignupDate())
                .build();
    }

    @Override
    @Transactional
    public void delete(String userProviderId) {

        if(!userRepository.existsByUserId(userProviderId)) {
            throw new NotFoundException("해당 아이디에 맞는 회원을 찾을 수 없습니다.");
        }

        // Redis에서 refreshToken 삭제
        redisService.deleteRefreshToken(userProviderId);
        // DB에서 회원 삭제
        userRepository.deleteByUserId(userProviderId);
    }

    @Override
    @Transactional
    public String updateImg(String userName, MultipartFile image){
        // 사용자 조회
        User user = userRepository.findByUserId(userName)
                .orElseThrow(() -> new NotFoundException("해당 아이디에 맞는 회원을 찾을 수 없습니다."));

        try {
            String beforeImg = user.getUserProfileImg(); // 사용자의 변경전 이미지
            String newImgUrl = null; // 사용자가 변경할 이미지

            if (image != null && !image.isEmpty()) {
                // 기존 이미지 S3에서 삭제
                s3Service.deleteS3(beforeImg);
                // 새 이미지 S3의 업로드
                newImgUrl = s3Service.uploadS3(image, "profileImg");
            }

            // 새 이미지 DB에 업데이트
            user.setUserProfileImg(newImgUrl);

            // DB 저장
            userRepository.save(user);

            return newImgUrl;

        } catch (Exception e) {
            throw new RuntimeException("프로필을 업데이트 하는데 에러가 발생했습니다.");
        }
    }

    @Override
    public void deleteImg(String userName){
        // 사용자 조회
        User user = userRepository.findByUserId(userName)
                .orElseThrow(() -> new NotFoundException("해당 userId의 맞는 회원을 찾을 수 없습니다."));

        try {

            String beforeImg = user.getUserProfileImg(); // 사용자의 변경전 이미지
            s3Service.deleteS3(beforeImg); // S3에서 변경 전 이미지 삭제

            // 기본 이미지 DB에 업데이트
            String basicImgUrl = "https://ssafy-gumi02-d209.s3.ap-northeast-2.amazonaws.com/profileImg/default.png";  // 앱 내 기본 이미지
            user.setUserProfileImg(basicImgUrl);

            // DB 저장
            userRepository.save(user);

        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지를 삭제하는데 에러가 발생했습니다.");
        }

    }

    @Override
    @Transactional
    public void updateNickname(String userName, String nickName) {
        if (!userRepository.existsByUserNickname(nickName)) {
            // 사용자 조회
            User user = userRepository.findByUserId(userName)
                    .orElseThrow(() -> new NotFoundException("해당 userId의 맞는 회원을 찾을 수 없습니다."));

            // 새로운 닉네임 DB에 업데이트
            user.setUserNickname(nickName);

            // DB 저장
            userRepository.save(user);
        } else {
            throw new UserNicknameDuplicateException(nickName + "을 닉네임으로 가진 회원이 이미 존재합니다.");
        }
    }

    @Override
    public boolean chkuserId(String userId) {

        if (userRepository.existsByUserId(userId)) {
            throw new UserNicknameDuplicateException("이미 사용중인 아이디입니다.");
        }

        return true;
    }

    @Override
    public User findByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("사용자를 찾을 수 없습니다. (ID: %s)", userId)));
    }

    @Override
    public User findByUserUid(Long userUid) {
        return userRepository.findByUserUid(userUid)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("사용자를 찾을 수 없습니다. (UID: %d)", userUid)));
    }
}

