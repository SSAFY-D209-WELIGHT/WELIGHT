package com.d209.welight.domain.user.service;

import com.d209.welight.domain.elasticsearch.event.UserEvent;
import com.d209.welight.domain.user.dto.request.FormSignUpRequestDTO;
import com.d209.welight.domain.user.dto.request.SocialSignUpRequestDTO;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.global.exception.user.UserCreationException;
import com.d209.welight.global.service.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // userEmail으로 사용자를 찾아 UserDetails객체를 반환
        return userRepository.findByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다."));
    }

    @Transactional
    public UserDetails createUserDetails(User user) {
        // UserDetails객체 생성
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getUserPassword())
                .roles(user.isUserIsAdmin() ? "ADMIN" : "USER")
                .build();
    }

    @Override
    @Transactional
    public void createUser(UserDetails user) {
        try {
            // 사용자가 이미 존재하는 경우
            if (userExists(user.getUsername()))
                throw new UserCreationException("사용자 이름을 가진 사용자가 이미 존재합니다." + user.getUsername());

            User newUser = (User) user;

            userRepository.save(newUser);
            eventPublisher.publishEvent(new UserEvent("CREATE", newUser));
        } catch (DataIntegrityViolationException e) {
            throw new UserCreationException("사용자를 데이터베이스에 저장하는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new UserCreationException("사용자 생성 중에 예기치 않은 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public void createUser
            (FormSignUpRequestDTO request, MultipartFile image) throws IOException {

        String imgUrl = "https://ssafy-gumi02-d209.s3.ap-northeast-2.amazonaws.com/profileImg/default.png";  // 앱 내 기본 이미지

        if(image != null && !image.isEmpty())
            imgUrl = s3Service.uploadS3(image, "profileImg");

        User user = User.builder()
                .userId(request.getUserId())
                .userPassword(passwordEncoder.encode(request.getUserPassword()))
                .userNickname(request.getUserNickname())
                .userProfileImg(imgUrl)
                .userLogin("Form")
                .userIsAdmin(request.isUserIsAdmin())
                .userSignupDate(LocalDateTime.now())
                .build();

        createUser(user); // 기존 createUser(UserDetails user) 호출
    }

    @Transactional
    public void createUser(SocialSignUpRequestDTO request, String image) {

        String imgUrl = "https://ssafy-gumi02-d209.s3.ap-northeast-2.amazonaws.com/profileImg/default.png";  // 앱 내 기본 이미지

        User user = User.builder()
                .userId(request.getUserId())
                .userPassword(passwordEncoder.encode(""))
                .userNickname(request.getUserNickname())
                .userProfileImg(imgUrl)
                .userLogin(request.getUserLogin())
                .userIsAdmin(false)
                .userSignupDate(LocalDateTime.now())
                .build();

        createUser(user); // 기존 createUser(UserDetails user) 호출
    }

    @Override
    @Transactional
    public void deleteUser(String userName) {

        User user = userRepository.findByUserId(userName)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원을 찾을 수 없습니다."));
        userRepository.deleteByUserId(userName);
        eventPublisher.publishEvent(new UserEvent("DELETE", user));

    }

    @Override
    @Transactional
    public void updateUser(UserDetails user) {
        userRepository.findByUserId(user.getUsername())
                .ifPresent(existingUser  -> {
                    existingUser .setUserPassword(passwordEncoder.encode(user.getPassword()));
                    existingUser .setUserIsAdmin(user.getAuthorities().stream()
                            // user가 가진 권한들 중 하나라도 "ROLE_ADMIN"이라는 권한이 있으면 true를 반환
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
                    userRepository.save(existingUser);
                    eventPublisher.publishEvent(new UserEvent("UPDATE", existingUser));

                });
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        // 현재 인증된 사용자의 비밀번호를 변경
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null)
            throw new AccessDeniedException("현재 사용자에 대한 컨텍스트에서 인증 개체를 찾을 수 없으므로 비밀번호를 변경할 수 없습니다.");

        String username = currentUser.getName();

        userRepository.findByUserId(username)
                .ifPresent(user -> {
                    if (passwordEncoder.matches(oldPassword, user.getUserPassword())) {
                        user.setUserPassword(passwordEncoder.encode(newPassword));
                        userRepository.save(user);
                    } else {
                        throw new BadCredentialsException("이전 비밀번호가 올바르지 않습니다.");
                    }
                });
    }

    @Override
    public boolean userExists(String userName) {
        // 회원 존재 여부 확인
        return userRepository.existsByUserId(userName);
    }
}
