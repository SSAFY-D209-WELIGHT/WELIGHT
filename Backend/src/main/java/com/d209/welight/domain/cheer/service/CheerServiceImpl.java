package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.dto.response.ParticipantsResponse;
import com.d209.welight.domain.cheer.entity.CheerParticipation;
import com.d209.welight.domain.cheer.entity.CheerParticipationId;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.domain.cheer.repository.CheerroomRepository;
import com.d209.welight.domain.cheer.repository.CheerParticipationRepository;
import com.d209.welight.domain.cheer.entity.Cheerroom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<CheerroomResponse> getAllCheerroomsByGeo(FindByGeoRequest findByGeoRequest) {
        List<Cheerroom> cheerrooms = cheerroomRepository.findByGeo(
                findByGeoRequest.getLatitude(),
                findByGeoRequest.getLongitude(),
                findByGeoRequest.getRadius()
        );

        return cheerrooms.stream()
                .map(CheerroomResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipantsResponse> getParticipants(Long cheerId) {
        Cheerroom cheerroom = cheerroomRepository.findById(cheerId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 응원방입니다."));
        List<CheerParticipation> cheerParticipationList = cheerroom.getParticipations();
        List<ParticipantsResponse> participantsResponses = new ArrayList<>();
        for (CheerParticipation participation : cheerParticipationList) {
            User user = participation.getUser();
            boolean isLeader = participation.isOwner();
            ParticipantsResponse participantInfo = ParticipantsResponse.builder()
                    .userNickname(user.getUserNickname())
                    .userProfileImg(user.getUserProfileImg())
                    .isLeader(isLeader)
                    .build();
            participantsResponses.add(participantInfo);
        }
        return participantsResponses;
    }

    @Override
    public void delegateLeader(long roomId, User currentLeader, User newLeader) {
        CheerParticipation currentLeaderCheerParticipation = cheerParticipationRepository
                .findByUserAndCheerroomId(currentLeader, roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다."));
        CheerParticipation newLeaderCheerParticipation = cheerParticipationRepository
                .findByUserAndCheerroomId(newLeader, roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다."));

        // CheerParticipation 테이블에서 새 방장의 cheerroom_is_owner 0->1
        newLeaderCheerParticipation.setOwner(true);
        // CheerParticipation 테이블에서 기존 방장의 cheerroom_is_owner 1->0
        currentLeaderCheerParticipation.setOwner(false);

        cheerParticipationRepository.save(currentLeaderCheerParticipation);
        cheerParticipationRepository.save(newLeaderCheerParticipation);

    }

    @Override
    public void endCheering(User user, long cheerId) {
        Cheerroom cheerroom = cheerroomRepository.findById(cheerId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 응원방입니다."));
        // 현재 유저가 방장인지 확인
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, cheerId)
                .orElseThrow(() -> new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다."));
        if (!participation.isOwner()) {
            throw new AccessDeniedException("방장만 응원을 종료할 수 있습니다.");
        }

        // is_done 값 true로 업데이트
        cheerroom.setDone(true);
        cheerParticipationRepository.save(participation);

        // cheerroom_display 업데이트 하는 로직 추후 작성
    }

    /* 기록 */
    @Override
    public void createRecords(User user, long roomId, CheerRecordRequest cheerRecordRequest) {
        // 참여한 응원 찾기
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다."));

        participation.updateCheerMemo(cheerRecordRequest.getCheerMemo());
    }

    @Override
    public void deleteRecords(User user, long roomId) {
        Cheerroom cheerroom = cheerroomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 응원방입니다."));
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, roomId)
                .orElseThrow(() -> new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다."));
        // 저장소에서 삭제
        cheerParticipationRepository.deleteByUserAndCheerroom(user, cheerroom);
    }
}