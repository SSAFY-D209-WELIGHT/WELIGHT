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
import org.springframework.security.access.AccessDeniedException;
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
    private static final String DEFAULT_THUMBNAIL_URL =
            "https://ssafy-gumi02-d209.s3.ap-northeast-2.amazonaws.com/default_thumbnail.png";

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
                .description(request.getCheerroomDescription())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDone(false)
                .createdAt(LocalDateTime.now())
                .build();

        Display defaultDisplay = displayRepository.findByDisplayThumbnailUrl(DEFAULT_THUMBNAIL_URL)
                .orElseThrow(() -> new IllegalArgumentException("기본 디스플레이를 찾을 수 없습니다."));


        Cheerroom savedCheerroom = cheerroomRepository.save(cheerroom);

        CheerroomDisplay defaultCheerroomDisplay = CheerroomDisplay.builder()
                .cheerroom(cheerroom)
                .display(defaultDisplay)
                .build();

        cheerroomDisplayRepository.save(defaultCheerroomDisplay);
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
        return CheerroomResponse.from(savedCheerroom, 1);
    }

    @Override
    public List<CheerroomResponse> getAllCheerroomsByGeo(FindByGeoRequest findByGeoRequest) {
        log.info("위치 기반 응원방 조회 시작 - 위도={}, 경도={}, 반경={}",
                findByGeoRequest.getLatitude(),
                findByGeoRequest.getLongitude(),
                findByGeoRequest.getRadius());
        List<Cheerroom> cheerrooms = cheerroomRepository.findByGeo(
                findByGeoRequest.getLatitude(),
                findByGeoRequest.getLongitude(),
                findByGeoRequest.getRadius()
        );

        log.info("위치 기반 응원방 조회 완료 - 조회된 응원방 개수={}", cheerrooms.size());
        return cheerrooms.stream()
                .map(cheerroom -> {
                    // 참가자 수 조회
                    int participantCount = cheerParticipationRepository
                            .countParticipantsByCheerroomId(cheerroom.getId());
                    // CheerroomResponse 생성
                    return CheerroomResponse.from(cheerroom, participantCount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipantsResponse> getParticipants(Long cheerId) {
        log.info("응원방 참여자 목록 조회 시작 - 응원방ID={}", cheerId);
        Cheerroom cheerroom = cheerroomRepository.findById(cheerId)
                .orElseThrow(() -> {
                    log.error("응원방을 찾을 수 없음 - 응원방ID={}", cheerId);
                    return new EntityNotFoundException("존재하지 않는 응원방입니다.");
                });
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
        log.info("응원방 참여자 목록 조회 완료 - 응원방ID={}, 참여자 수={}",
                cheerId, participantsResponses.size());
        return participantsResponses;
    }

    @Override
    public void delegateLeader(long roomId, User currentLeader, User newLeader) {
        log.info("방장 권한 위임 시작 - 응원방ID={}, 현재방장UID={}, 새로운방장UID={}",
                roomId, currentLeader.getUserUid(), newLeader.getUserUid());

        CheerParticipation currentLeaderCheerParticipation = cheerParticipationRepository
                .findByUserAndCheerroomId(currentLeader, roomId)
                .orElseThrow(() -> {
                    log.error("현재 방장의 참여 정보를 찾을 수 없음 - 사용자UID={}, 응원방ID={}",
                            currentLeader.getUserUid(), roomId);
                    return new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다.");
                });
        CheerParticipation newLeaderCheerParticipation = cheerParticipationRepository
                .findByUserAndCheerroomId(newLeader, roomId)
                .orElseThrow(() -> {
                    log.error("새로운 방장의 참여 정보를 찾을 수 없음 - 사용자UID={}, 응원방ID={}",
                            newLeader.getUserUid(), roomId);
                    return new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다.");
                });

        // CheerParticipation 테이블에서 새 방장의 cheerroom_is_owner 0->1
        newLeaderCheerParticipation.setOwner(true);
        // CheerParticipation 테이블에서 기존 방장의 cheerroom_is_owner 1->0
        currentLeaderCheerParticipation.setOwner(false);

        cheerParticipationRepository.save(currentLeaderCheerParticipation);
        cheerParticipationRepository.save(newLeaderCheerParticipation);

        log.info("방장 권한 위임 완료 - 응원방ID={}, 새로운방장UID={}",
                roomId, newLeader.getUserUid());

    }

    @Override
    public void endCheering(User user, long cheerId) {
        log.info("응원방 종료 처리 시작 - 사용자ID={}, 응원방ID={}", user.getUserId(), cheerId);

        Cheerroom cheerroom = cheerroomRepository.findById(cheerId)
                .orElseThrow(() -> {
                    log.error("응원방을 찾을 수 없음 - 응원방ID={}", cheerId);
                    return new EntityNotFoundException("존재하지 않는 응원방입니다.");
                });
        // 현재 유저가 방장인지 확인
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, cheerId)
                .orElseThrow(() -> {
                    log.error("참여 정보를 찾을 수 없음 - 사용자ID={}, 응원방ID={}",
                            user.getUserId(), cheerId);
                    return new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다.");
                });
        if (!participation.isOwner()) {
            log.error("권한 없음 - 사용자ID={}, 응원방ID={}", user.getUserId(), cheerId);
            throw new AccessDeniedException("방장만 응원을 종료할 수 있습니다.");
        }

        // is_done 값 true로 업데이트
        cheerroom.setDone(true);
        cheerParticipationRepository.save(participation);
        log.info("응원방 종료 처리 완료 - 응원방ID={}", cheerId);

        // cheerroom_display 업데이트 -> 응원방 디스플레이 선택저장 API
    }

    /* 기록 */
    @Override
    public void createRecords(User user, long roomId, CheerRecordRequest cheerRecordRequest) {
        log.info("응원 기록 생성 시작 - 사용자ID={}, 응원방ID={}", user.getUserId(), roomId);
        // 참여한 응원 찾기
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, roomId)
                .orElseThrow(() -> {
                    log.error("참여 정보를 찾을 수 없음 - 사용자ID={}, 응원방ID={}",
                            user.getUserId(), roomId);
                    return new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다.");
                });

        participation.updateCheerMemo(cheerRecordRequest.getCheerMemo());
    }

    @Override
    public void deleteRecords(User user, long roomId) {
        log.info("응원 기록 삭제 시작 - 사용자ID={}, 응원방ID={}", user.getUserId(), roomId);

        Cheerroom cheerroom = cheerroomRepository.findById(roomId)
                .orElseThrow(() -> {
                    log.error("응원방을 찾을 수 없음 - 응원방ID={}", roomId);
                    return new EntityNotFoundException("존재하지 않는 응원방입니다.");
                });
                cheerParticipationRepository
                .findByUserAndCheerroomId(user, roomId)
                .orElseThrow(() -> {
                    log.error("참여 정보를 찾을 수 없음 - 사용자ID={}, 응원방ID={}",
                            user.getUserId(), roomId);
                    return new EntityNotFoundException("해당 응원방에 참여하지 않은 사용자입니다.");
                });
        // 저장소에서 삭제
        cheerParticipationRepository.deleteByUserAndCheerroom(user, cheerroom);
        log.info("응원 기록 삭제 완료 - 사용자ID={}, 응원방ID={}", user.getUserId(), roomId);
    }

    public void enterCheerroom(String userId, Long cheerroomId) {
        log.info("응원방 입장 시작 - 사용자ID={}, 응원방ID={}", userId, cheerroomId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없음 - 사용자ID={}", userId);
                    return new IllegalArgumentException("사용자를 찾을 수 없습니다.");
                });

        Cheerroom cheerroom = cheerroomRepository.findById(cheerroomId)
                .orElseThrow(() -> {
                    log.error("응원방을 찾을 수 없음 - 응원방ID={}", cheerroomId);
                    return new IllegalArgumentException("응원방을 찾을 수 없습니다.");
                });

        CheerParticipationId participationId = new CheerParticipationId(user.getUserUid(), cheerroomId);

        CheerParticipation participation = cheerParticipationRepository
                .findById(participationId)
                .orElse(null);

        if (participation == null) {
            log.debug("신규 참여자 입장 - 사용자ID={}, 응원방ID={}", userId, cheerroomId);
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
        log.info("응원방 퇴장 처리 시작 - 사용자ID={}, 응원방ID={}", userId, cheerroomId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CheerParticipationId participationId = new CheerParticipationId(user.getUserUid(), cheerroomId);

        CheerParticipation participation = cheerParticipationRepository
                .findById(participationId)
                .orElseThrow(() -> {
                    log.error("참여 정보를 찾을 수 없음 - 사용자ID={}, 응원방ID={}", userId, cheerroomId);
                    return new IllegalArgumentException("참여 정보를 찾을 수 없습니다.");
                });

        LocalDateTime exitTime = LocalDateTime.now();

        if (participation.isOwner()) {
            log.debug("방장 퇴장 처리 시작 - 응원방ID={}", cheerroomId);
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

            log.info("방장 퇴장으로 인한 응원방 종료 처리 완료 - 응원방ID={}, 퇴장처리된 참여자 수={}",
                    cheerroomId, activeParticipations.size());
        } else {
            // 방장이 아닌 경우: 본인만 퇴장 처리
            updateExitInfo(participation, exitTime);
            cheerParticipationRepository.save(participation);
            log.info("참여자 퇴장 처리 완료 - userId: {}, cheerroomId: {}", userId, cheerroomId);
        }
    }

    public List<CheerHistoryResponse> getUserCheerHistory(String userId) {
        log.info("사용자 응원 히스토리 조회 시작 - 사용자ID={}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<CheerParticipation> participations =
                cheerParticipationRepository.findUserParticipationHistory(user.getUserUid());

        List<CheerHistoryResponse> historyResponses = participations.stream()
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
        log.info("사용자 응원 히스토리 조회 완료 - 사용자ID={}, 히스토리 수={}",
                userId, historyResponses.size());
        return historyResponses;
    }

    public CheerHistoryDetailResponse getCheerHistoryDetail(String userId, Long cheerroomUid) {
        log.info("응원 히스토리 상세 조회 시작 - 사용자ID={}, 응원방ID={}", userId, cheerroomUid);

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

        log.info("응원 히스토리 상세 조회 완료 - 사용자ID={}, 응원방ID={}, 총 참여시간={}",
                userId, cheerroomUid,totalDuration);
        return CheerHistoryDetailResponse.builder()
                .participationDate(participation.getLastExitTime().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd a h시")))
                .cheerroomName(participation.getCheerroom().getName())
                .cheerroomDescription(participation.getCheerroom().getDescription())
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

    @Override
    public CheerHistoryResponse useDisplayForCheer(Long cheerroomId, String userId, Long displayId) {
        // 사용자 검증
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 응원방 검증
        Cheerroom cheerroom = cheerroomRepository.findById(cheerroomId)
                .orElseThrow(() -> new IllegalArgumentException("응원방을 찾을 수 없습니다."));

        // 방장 권한 검증
        CheerParticipation participation = cheerParticipationRepository
                .findByUserAndCheerroomId(user, cheerroomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 응원방에 참여하지 않은 사용자입니다."));
        if (!participation.isOwner()) {
            throw new IllegalArgumentException("방장만 디스플레이를 선택할 수 있습니다.");
        }

        // default.png 제거 (첫 디스플레이 선택 시에만)
        if (cheerroom.getDisplays().size() == 1 &&
                cheerroom.getDisplays().get(0).getDisplay().getDisplayThumbnailUrl().equals(DEFAULT_THUMBNAIL_URL)) {
            cheerroomDisplayRepository.delete(cheerroom.getDisplays().get(0));
            cheerroom.getDisplays().clear();
        }

        // 새로운 디스플레이 추가
        Display display = displayRepository.findByDisplayUid(displayId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 디스플레이입니다."));

        CheerroomDisplay cheerroomDisplay = CheerroomDisplay.builder()
                .cheerroom(cheerroom)
                .display(display)
                .build();

        cheerroomDisplayRepository.save(cheerroomDisplay);

        // 최신 디스플레이 목록 조회
        List<CheerroomDisplay> displays = cheerroomDisplayRepository.findByCheerroom(cheerroom);

        List<CheerDisplayInfo> displayInfos = displays.stream()
                .map(cd -> CheerDisplayInfo.builder()
                        .displayUid(cd.getDisplay().getDisplayUid())
                        .displayName(cd.getDisplay().getDisplayName())
                        .thumbnailUrl(cd.getDisplay().getDisplayThumbnailUrl())
                        .usedAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        log.info("응원방 디스플레이 업데이트 완료 - cheerroomId: {}, displayId: {}, 총 디스플레이 수: {}",
                cheerroomId, displayId, displayInfos.size());

        return CheerHistoryResponse.builder()
                .cheerroomName(cheerroom.getName())
                .participationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd a h시")))
                .participantCount(cheerParticipationRepository.countParticipantsByCheerroomId(cheerroomId))
                .displays(displayInfos)
                .build();

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