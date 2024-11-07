package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.dto.response.ParticipantsResponse;
import com.d209.welight.domain.cheer.entity.cheerparticipation.CheerParticipation;
import com.d209.welight.domain.cheer.entity.cheerparticipation.CheerParticipationId;

import com.d209.welight.domain.cheer.dto.CheerDisplayInfo;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryDetailResponse;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryResponse;
import com.d209.welight.domain.cheer.entity.cheerroomdisplay.CheerroomDisplay;
import com.d209.welight.domain.cheer.repository.CheerroomDisplayRepository;
import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.repository.DisplayRepository;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.domain.cheer.repository.CheerroomRepository;
import com.d209.welight.domain.cheer.repository.CheerParticipationRepository;
import com.d209.welight.domain.cheer.entity.Cheerroom;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final DisplayRepository displayRepository;
    private final CheerroomDisplayRepository cheerroomDisplayRepository;

    @Override
    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request) {
        log.info("응원방 생성 시작 - userId: {}, cheerroomName: {}, location: [{}, {}]",
                userId, request.getCheerroomName(), request.getLatitude(), request.getLongitude());

        // 응원방 이름 중복 검사
        List<Cheerroom> existingCheerrooms = cheerroomRepository.findAllByName(request.getCheerroomName());
        boolean hasActiveCheerroom = existingCheerrooms.stream()
                .anyMatch(cheerroom -> !cheerroom.isDone());

        if (hasActiveCheerroom) {
            log.error("중복된 응원방 이름 - cheerroomName: {}", request.getCheerroomName());
            throw new IllegalArgumentException("이미 존재하는 응원방 이름입니다.");
        }

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

    public List<CheerHistoryResponse> getUserCheerHistory(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<CheerParticipation> participations =
                cheerParticipationRepository.findUserParticipationHistory(user.getUserUid());

        return participations.stream()
                .map(participation -> CheerHistoryResponse.builder()
                        .participationDate(participation.getLastExitTime().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd a h시")))
                        .cheerroomName(participation.getCheerroom().getName())
                        .participantCount(cheerParticipationRepository
                                .countParticipantsByCheerroomId(participation.getCheerroom().getId()))
                        .memo(participation.getMemo())
                        .displays(participation.getCheerroom().getDisplays().stream()
                                .map(cheerroomDisplay -> CheerDisplayInfo.builder()
                                        .thumbnailUrl(cheerroomDisplay.getDisplay().getDisplayThumbnailUrl())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public CheerHistoryDetailResponse getCheerHistoryDetail(String userId, Long cheerroomUid) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroom(user.getUserUid(), cheerroomUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 응원 기록을 찾을 수 없습니다."));

        String durationStr = String.valueOf(participation.getTotalDuration());
        LocalTime duration = LocalTime.parse(durationStr);
        String totalDuration = String.format("%d시간 %d분",
                duration.getHour(),
                duration.getMinute());

        return CheerHistoryDetailResponse.builder()
                .participationDate(participation.getLastExitTime().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd a h시")))
                .cheerroomName(participation.getCheerroom().getName())
                .participantCount(cheerParticipationRepository
                        .countParticipantsByCheerroomId(participation.getCheerroom().getId()))
                .memo(participation.getMemo())
                .displays(participation.getCheerroom().getDisplays().stream()
                        .map(display -> CheerDisplayInfo.builder()
                                .thumbnailUrl(display.getDisplay().getDisplayThumbnailUrl())
                                .build())
                        .collect(Collectors.toList()))
                .totalDuration(totalDuration)
                .latitude(participation.getCheerroom().getLatitude())
                .longitude(participation.getCheerroom().getLongitude())
                .build();
    }

//    @Override
//    public void useDisplayForCheer(Long cheerroomId, String userId, List<Long> displayIds) {
//        // 사용자 검증
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//
//        // 응원방 검증
//        Cheerroom cheerroom = cheerroomRepository.findById(cheerroomId)
//                .orElseThrow(() -> new IllegalArgumentException("응원방을 찾을 수 없습니다."));
//
//        // 방장 권한 검증
//        CheerParticipation participation = cheerParticipationRepository
//                .findByUserAndCheerroomId(user, cheerroomId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 응원방에 참여하지 않은 사용자입니다."));
//        if (!participation.isOwner()) {
//            throw new IllegalArgumentException("방장만 디스플레이를 선택할 수 있습니다.");
//        }
//
//        // default.png 제거 (첫 디스플레이 선택 시에만)
//        if (cheerroom.getDisplays().size() == 1 &&
//                cheerroom.getDisplays().get(0).getDisplay().getDisplayThumbnailUrl().equals("default.png")) {
//            cheerroomDisplayRepository.delete(cheerroom.getDisplays().get(0));
//            cheerroom.getDisplays().clear();
//        }
//
//        // 새로운 디스플레이 추가
//        displayIds.forEach(displayId -> {
//            Display display = displayRepository.findById(displayId)
//                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 디스플레이입니다."));
//
//            CheerroomDisplay cheerroomDisplay = CheerroomDisplay.builder()
//                    .cheerroom(cheerroom)
//                    .display(display)
//                    .build();
//
//            cheerroomDisplayRepository.save(cheerroomDisplay);
//        });
//
//        log.info("응원방 디스플레이 업데이트 완료 - cheerroomId: {}, displayCount: {}",
//                cheerroomId, displayIds.size());
//
//    }

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