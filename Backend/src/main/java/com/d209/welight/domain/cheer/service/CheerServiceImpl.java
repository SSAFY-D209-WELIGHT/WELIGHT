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


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
                .lastEntryTime(LocalDateTime.now())
                .isOwner(true)
                .build();

        cheerParticipationRepository.save(participation);
        log.info("방장 참여 정보 생성 완료 - userUid: {}, cheerroomId: {}", userUid, savedCheerroom.getId());
        return CheerroomResponse.from(savedCheerroom);
    }

    @Override
    public void enterCheerroom(String userId, Long cheerroomId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Cheerroom cheerroom = cheerroomRepository.findById(cheerroomId)
                .orElseThrow(() -> new IllegalArgumentException("응원방을 찾을 수 없습니다."));

        CheerParticipationId participationId = new CheerParticipationId(user.getUserUid(), cheerroomId);

        CheerParticipation participation = cheerParticipationRepository
                .findById(participationId)
                .orElse(null);

        if (participation == null) {
            // 새로운 참여자
            participation = CheerParticipation.builder()
                    .id(participationId)
                    .user(user)
                    .cheerroom(cheerroom)
                    .participationDate(LocalDateTime.now())
                    .lastEntryTime(LocalDateTime.now())
                    .entryCount(1)
                    .isOwner(false)
                    .totalDuration(LocalTime.of(0, 0, 0))
                    .build();
        } else {
            // 재입장
            participation.setLastEntryTime(LocalDateTime.now());
            participation.setLastExitTime(null);
            participation.setEntryCount(participation.getEntryCount() + 1);
        }

        cheerParticipationRepository.save(participation);
        log.info("응원방 입장 완료 - userId: {}, cheerroomId: {}, entryCount: {}",
                userId, cheerroomId, participation.getEntryCount());
    }

    @Override
    @Transactional
    public void leaveCheerroom(String userId, Long cheerroomId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CheerParticipationId participationId = new CheerParticipationId(user.getUserUid(), cheerroomId);

        CheerParticipation participation = cheerParticipationRepository
                .findById(participationId)
                .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        LocalDateTime exitTime = LocalDateTime.now();

        if (participation.isOwner()) {
            // 방장이 나가는 경우: 응원방 종료 및 모든 참여자 퇴장 처리
            Cheerroom cheerroom = participation.getCheerroom();
            cheerroom.setDone(true);
            cheerroomRepository.save(cheerroom);

            List<CheerParticipation> activeParticipations = cheerParticipationRepository
                    .findByCheerroomAndLastExitTimeIsNull(cheerroom);

            for (CheerParticipation activeParticipation : activeParticipations) {
                updateExitInfo(activeParticipation, exitTime);
                cheerParticipationRepository.save(activeParticipation);
            }

            log.info("응원방 종료 처리 완료 - cheerroomId: {}, 퇴장 처리된 참여자 수: {}",
                    cheerroomId, activeParticipations.size());
        } else {
            // 방장이 아닌 경우: 본인만 퇴장 처리
            updateExitInfo(participation, exitTime);
            cheerParticipationRepository.save(participation);
            log.info("참여자 퇴장 처리 완료 - userId: {}, cheerroomId: {}", userId, cheerroomId);
        }
    }

    private void updateExitInfo(CheerParticipation participation, LocalDateTime exitTime) {
        participation.setLastExitTime(exitTime);
        Duration cheerDuration = Duration.between(participation.getLastEntryTime(), exitTime);
        LocalTime currentTotal = participation.getTotalDuration();
        LocalTime newTotal = currentTotal.plusHours(cheerDuration.toHours())
                .plusMinutes(cheerDuration.toMinutesPart())
                .plusSeconds(cheerDuration.toSecondsPart());
        participation.setTotalDuration(newTotal);

        log.debug("퇴장 정보 업데이트 - userId: {}, totalDuration: {}",
                participation.getUser().getUserId(), newTotal);
    }
}