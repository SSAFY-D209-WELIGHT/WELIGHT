package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.entity.CheerParticipation;
import com.d209.welight.domain.cheer.entity.CheerParticipationId;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.domain.cheer.repository.CheerroomRepository;
import com.d209.welight.domain.cheer.repository.CheerParticipationRepository;
import com.d209.welight.domain.cheer.entity.Cheerroom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CheerServiceImpl implements CheerService {

    private final CheerroomRepository cheerroomRepository;
    private final CheerParticipationRepository cheerParticipationRepository;
    private final UserRepository userRepository;

    @Override
    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request) {
        log.info("응원방 생성 시작 - userId: {}, cheerroomName: {}, location: [{}, {}]",
                userId, request.getCheerroomName(), request.getLatitude(), request.getLongitude());

        // 위도/경도 유효성 검사
        if (request.getLatitude() < -90 || request.getLatitude() > 90) {
            log.error("잘못된 위도 값 - latitude: {}", request.getLatitude());
            throw new IllegalArgumentException("위도는 -90도에서 90도 사이의 값이어야 합니다.");
        }
        if (request.getLongitude() < -180 || request.getLongitude() > 180) {
            log.error("잘못된 경도 값 - longitude: {}", request.getLongitude());
            throw new IllegalArgumentException("경도는 -180도에서 180도 사이의 값이어야 합니다.");
        }
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("사용자 조회 실패 - userId: {}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        Long userUid = user.getUserUid();
        log.debug("사용자 조회 성공 - userUid: {}", userUid);

        // 응원방 생성
        Cheerroom cheerroom = Cheerroom.builder()
                .name(request.getCheerroomName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDone(false)
                .createdAt(LocalDateTime.now())
                .build();

        Cheerroom savedCheerroom = cheerroomRepository.save(cheerroom);
        log.info("응원방 생성 완료 - cheerroomId: {}", savedCheerroom.getId());

        // 방장 참여 정보 생성
        CheerParticipation participation = CheerParticipation.builder()
                .id(new CheerParticipationId(userUid, savedCheerroom.getId()))
                .user(user)
                .cheerroom(savedCheerroom)
                .participationDate(LocalDateTime.now())
                .isOwner(true)
                .build();

        cheerParticipationRepository.save(participation);
        log.info("방장 참여 정보 생성 완료 - userUid: {}, cheerroomId: {}", userUid, savedCheerroom.getId());
        return CheerroomResponse.from(savedCheerroom);
    }

    @Override
    public void enterCheerroom(String userId, Long cheerroomId) {
        log.info("응원방 참여 시작 - userId: {}, cheerroomId: {}", userId, cheerroomId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("사용자 조회 실패 - userId: {}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        Cheerroom cheerroom = cheerroomRepository.findById(cheerroomId)
                .orElseThrow(() -> {
                    log.error("응원방 조회 실패 - cheerroomId: {}", cheerroomId);
                    return new IllegalArgumentException("응원방을 찾을 수 없습니다.");
                });

        // 이미 참여한 사용자인지 확인
        if (cheerParticipationRepository.existsById(new CheerParticipationId(user.getUserUid(), cheerroomId))) {
            log.error("이미 참여한 응원방입니다 - userId: {}, cheerroomId: {}", userId, cheerroomId);
            throw new IllegalStateException("이미 참여한 응원방입니다.");
        }

        CheerParticipation participation = CheerParticipation.builder()
                .id(new CheerParticipationId(user.getUserUid(), cheerroomId))
                .user(user)
                .cheerroom(cheerroom)
                .participationDate(LocalDateTime.now())
                .isOwner(false)  // 참여자는 방장이 아님
                .build();

        cheerParticipationRepository.save(participation);
        log.info("응원방 참여 완료 - userId: {}, cheerroomId: {}", userId, cheerroomId);
    }
}