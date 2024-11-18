package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.DisplayImageDto;
import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.*;
import com.d209.welight.domain.display.entity.*;
import com.d209.welight.domain.display.entity.displaylike.DisplayLike;
import com.d209.welight.domain.display.entity.displaystorage.DisplayStorage;
import com.d209.welight.domain.elasticsearch.event.DisplayEvent;
import com.d209.welight.domain.display.repository.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.global.exception.common.NotFoundException;
import com.d209.welight.global.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisplayServiceImpl implements DisplayService {

    private final DisplayRepository displayRepository;
    private final DisplayTagRepository displayTagRepository;
    private final DisplayImageRepository displayImageRepository;
    private final DisplayTextRepository displayTextRepository;
    private final DisplayBackgroundRepository displayBackgroundRepository;
    private final DisplayStorageRepository displayStorageRepository;
    private final DisplayLikeRepository displayLikeRepository;
    private final DisplayCommentRepository displayCommentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    private final ApplicationEventPublisher eventPublisher;
    private final DisplayHelper displayHelper;

    @Override
    @Transactional
    @CacheEvict(value = {"allDisplays","myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse createDisplay(String userId, DisplayCreateRequest request) {

        displayHelper.validateDisplayCreateRequest(request);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        try {

            // 디스플레이 기본 정보 생성 및 저장
            Display savedDisplay = displayHelper.createAndSaveDisplay(user, request);

            // 이벤트 발행
            eventPublisher.publishEvent(new DisplayEvent("CREATE", savedDisplay));

            // 태그가 있을 때만 저장
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                displayHelper.saveTags(savedDisplay, request.getTags());
            }
            
            // 이미지가 있을 때만 저장
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                displayHelper.saveImages(savedDisplay, request.getImages());
            }
            
            // 텍스트가 있을 때만 저장
            if (request.getTexts() != null && !request.getTexts().isEmpty()) {
                displayHelper.saveTexts(savedDisplay, request.getTexts());
            }

            // 배경은 필수값이므로 그대로 저장
            displayHelper.saveBackground(savedDisplay, request.getBackground());
            displayHelper.saveDisplayStorage(user, savedDisplay);

            log.info("사용자 {}의 디스플레이 ID: {} 생성 완료", userId, savedDisplay.getDisplayUid());

            return DisplayCreateResponse.builder()
                    .displayUid(savedDisplay.getDisplayUid())
                    .displayName(savedDisplay.getDisplayName())
                    .message("디스플레이가 성공적으로 생성되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("디스플레이 생성 중 예기치 않은 오류: {}", e.getMessage());
            throw new RuntimeException("디스플레이 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Cacheable(value = "displayDetails", key = "#request.displayUid")
    @Override
    public DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request) {

        // Display 존재 여부 확인
        Display display = displayRepository.findById(request.getDisplayUid())
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        try {
            // 태그 정보 조회
            List<DisplayTag> tags = displayTagRepository.findByDisplay(display);

            // 현재 사용자의 userId를 userUid로 변환하여 비교
            boolean isOwner = false;
            boolean isFavorite = false;
            boolean isLiked = false;
            boolean isStored = false;

            // 제작자 정보 조회
            User creator = userRepository.findByUserUid(display.getCreatorUid())
                  .orElseThrow(() -> new EntityNotFoundException("제작자 정보를 찾을 수 없습니다."));
            String creatorName = creator.getUsername();

            if (request.getUserId() != null) {
                Optional<User> currentUser = userRepository.findByUserId(request.getUserId());
                if (currentUser.isPresent()) {

                    // 제작자 인지
                    isOwner = display.getCreatorUid().equals(currentUser.get().getUserUid());

                    // 저장되어 있는지
                    isStored = displayStorageRepository.existsByUserAndDisplay(currentUser.get(), display);

                    // 즐겨찾기 여부 확인
                    isFavorite = displayStorageRepository.existsByUserAndDisplayAndIsFavoritesIsTrue(currentUser, display);
                    // 좋아요 여부 확인
                    isLiked = displayLikeRepository.existsByUserAndDisplay(currentUser, display);
                }
            }

            log.info("디스플레이 상세 정보 조회 완료: 디스플레이 ID {}", request.getDisplayUid());

            // Response 객체 생성 및 반환
            return DisplayDetailResponse.builder()
                    .creatorUid(display.getCreatorUid())
                    .creatorName(creatorName)
                    .displayName(display.getDisplayName())
                    .displayThumbnailUrl(display.getDisplayThumbnailUrl())
                    .displayIsPosted(display.getDisplayIsPosted())
                    .tags(tags.stream()
                            .map(DisplayTag::getDisplayTagText)
                            .collect(Collectors.toList()))
                    .isOwner(isOwner)
                    .isFavorite(isFavorite)
                    .isLiked(isLiked)
                    .isStored(isStored)
                    .likeCount(display.getDisplayLikeCount())
                    .downloadCount(display.getDisplayDownloadCount())
                    .images(displayHelper.convertImagesToDto(display.getImages()))
                    .background(displayHelper.convertBackgroundToDto(display.getBackground()))
                    .texts(displayHelper.convertTextsToDto(display.getTexts()))
                    .commentCount(displayCommentRepository.countByDisplay(display))
                    .build();

        } catch (Exception e) {
            log.error("디스플레이 상세 조회 중 예외 발생", e);
            throw e;
        }
    }

    @Cacheable(value = "allDisplays", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    @Override
    public DisplayListResponse getDisplayList(Pageable pageable) {
        try {
            Page<Display> displays = displayRepository.findAllByDisplayIsPostedTrue(pageable);

            List<DisplayListResponse.DisplayInfo> displayInfos = displays.getContent().stream()
                .map(display -> DisplayListResponse.DisplayInfo.builder()
                    .displayUid(display.getDisplayUid())
                    .displayThumbnail(display.getDisplayThumbnailUrl())
                    .build())
                .collect(Collectors.toList());

            log.info("디스플레이 목록 조회 완료: {} 개의 디스플레이 반환", displayInfos.size());

            return DisplayListResponse.builder()
                .currentPage(pageable.getPageNumber())
                .displays(displayInfos)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @Cacheable(value = "myDisplays", key = "{#userId, #pageable}")
    @Override
    public DisplayListResponse getMyDisplayList(String userId, Pageable pageable) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        try {

            // 사용자 id를 통해 userUid 조회
            Long userUid = user.getUserUid();

            // 내가 저장한 디스플레이 조회
            Page<Display> displays = displayRepository.findAllStoredByUser(user, pageable);

            // 사용자의 디스플레이 목록 조회
            List<DisplayListResponse.DisplayInfo> displayInfos = displays.getContent().stream()
                    .map(display -> {
                        // DisplayStorage에서 즐겨찾기 여부 확인
                        boolean isFavorite = displayStorageRepository.findByUserAndDisplay(user, display)
                                .map(DisplayStorage::getIsFavorites)
                                .orElse(false);

                        return DisplayListResponse.DisplayInfo.builder()
                                .displayUid(display.getDisplayUid())
                                .displayThumbnail(display.getDisplayThumbnailUrl())
                                .isFavorite(isFavorite)  // 즐겨찾기 여부 추가
                                .build();
                    })
                    .collect(Collectors.toList());

            log.info("사용자 {}의 디스플레이 목록 조회 완료: {} 개의 디스플레이 반환", userId, displayInfos.size());

            return DisplayListResponse.builder()
                .currentPage(pageable.getPageNumber())
                .displays(displayInfos)
                .build();
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("내 디스플레이 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allDisplays","myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse duplicateDisplay(Long displayId, String userId) {
        // 원본 디스플레이 조회
        Display originalDisplay = displayRepository.findById(displayId)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        // userUid로 userId 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Long userUid = user.getUserUid();

        // 새로운 디스플레이 생성
        Display newDisplay = Display.builder()
                .creatorUid(userUid)
                .displayName(originalDisplay.getDisplayName() + "_복제")
                .displayIsPosted(false)
                .displayDownloadCount(0L)
                .displayLikeCount(0L)
                .build();

        // 썸네일 복제
        try {
            String originalThumbnailUrl = originalDisplay.getDisplayThumbnailUrl();
            if (originalThumbnailUrl != null && !originalThumbnailUrl.isEmpty()) {
                // 새로운 썸네일 파일명 생성
                String newThumbnailName = displayHelper.generateFileName(userId, "thumbnails", originalThumbnailUrl);

                // S3에 썸네일 복사
                String newThumbnailUrl = s3Service.copyS3(
                        originalThumbnailUrl,
                        newThumbnailName
                );

                // 새로운 디스플레이에 썸네일 URL 설정
                newDisplay.setDisplayThumbnailUrl(newThumbnailUrl);
            }
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("디스플레이를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 복제 중 오류가 발생했습니다.");
        }

        // 디스플레이 정보 저장
        Display savedDisplay = displayRepository.save(newDisplay);

        // 배경, 텍스트, 이미지 복제
        displayHelper.duplicateBackground(originalDisplay.getBackground(), savedDisplay);
        displayHelper.duplicateTexts(originalDisplay.getTexts(), savedDisplay);
        displayHelper.duplicateImages(originalDisplay.getImages(), savedDisplay, userId);

        // displayStorage 생성 및 저장
        displayHelper.saveDisplayStorage(user, savedDisplay);

        eventPublisher.publishEvent(new DisplayEvent("CREATE", savedDisplay));

        // 응답 객체 생성 및 반환
        return DisplayCreateResponse.builder()
                .displayUid(savedDisplay.getDisplayUid())
                .displayName(savedDisplay.getDisplayName())
                .message("디스플레이가 성공적으로 복제되었습니다.")
                .build();
    }

    @Override
    public DisplayCreateRequest getDisplayForEdit(Long displayId, String userId) {
        try {
            // 권한 확인
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            Display display = displayRepository.findById(displayId)
                    .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

            if (!display.getCreatorUid().equals(user.getUserUid())) {
                throw new IllegalStateException("디스플레이를 수정할 권한이 없습니다.");
            }

            // DisplayCreateRequest 생성 및 반환
            return DisplayCreateRequest.builder()
                    .displayName(display.getDisplayName())
                    .displayThumbnailUrl(display.getDisplayThumbnailUrl())
                    .displayIsPosted(display.getDisplayIsPosted())
                    .tags(displayHelper.getTagsFromDisplay(display, null))
                    .images(displayHelper.getImagesFromDisplay(display))
                    .texts(displayHelper.getTextsFromDisplay(display))
                    .background(displayHelper.getBackgroundFromDisplay(display))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("디스플레이 수정 정보 조회 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    @Override
    @CacheEvict(value = {"allDisplays","myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse updateDisplay(Long displayId, DisplayCreateRequest request, String userId) {
        // 1. 권한 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Display originalDisplay = displayRepository.findById(displayId)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        if (!originalDisplay.getCreatorUid().equals(user.getUserUid())) {
            throw new IllegalStateException("디스플레이를 수정할 권한이 없습니다.");
        }

        try {
            // 2. 기존 디스플레이 업데이트
            originalDisplay.setDisplayName(request.getDisplayName());
            originalDisplay.setDisplayThumbnailUrl(request.getDisplayThumbnailUrl());
            originalDisplay.setDisplayIsPosted(request.getDisplayIsPosted());

            // 3. 기존 관련 데이터 삭제
            originalDisplay.getTags().clear();
            originalDisplay.getImages().clear();
            originalDisplay.getTexts().clear();
            originalDisplay.setBackground(null);

            if (request.getTags() != null) {
                originalDisplay.getTags().clear(); // 기존 태그 비우기
                List<DisplayTag> displayTags = request.getTags().stream()
                        .map(tag -> new DisplayTag(originalDisplay, tag))
                        .collect(Collectors.toList());
                originalDisplay.getTags().addAll(displayTags); // 새 태그 추가
            }
            
            if (request.getImages() != null) {
                originalDisplay.getImages().clear(); // 기존 이미지 비우기
                List<DisplayImage> displayImages = request.getImages().stream()
                        .map(image -> new DisplayImage(originalDisplay, image))
                        .collect(Collectors.toList());
                originalDisplay.getImages().addAll(displayImages); // 새 이미지 추가
            }
            
            if (request.getTexts() != null) {
                originalDisplay.getTexts().clear(); // 기존 텍스트 비우기
                List<DisplayText> displayTexts = request.getTexts().stream()
                        .map(text -> new DisplayText(originalDisplay, text))
                        .collect(Collectors.toList());
                originalDisplay.getTexts().addAll(displayTexts); // 새 텍스트 추가
            }
            
            if (request.getBackground() != null) {
                originalDisplay.setBackground(new DisplayBackground(originalDisplay, request.getBackground()));
            }

            eventPublisher.publishEvent(new DisplayEvent("UPDATE", originalDisplay));
            log.info("디스플레이 ID: {}의 수정이 완료되었습니다.", originalDisplay.getDisplayUid());

            return DisplayCreateResponse.builder()
                    .displayUid(originalDisplay.getDisplayUid())
                    .displayName(originalDisplay.getDisplayName())
                    .message("디스플레이가 성공적으로 수정되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("디스플레이 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("디스플레이 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allDisplays","myDisplays", "displayDetails"}, allEntries = true)
    public void deleteDisplay(Long displayUid, String userId) {
        // 1. Display와 User 정보 확인
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 권한 확인 (생성자 또는 관리자만 삭제 가능)
        if (!display.getCreatorUid().equals(user.getUserUid()) && !user.isUserIsAdmin()) {
            throw new IllegalStateException("디스플레이를 삭제할 권한이 없습니다.");
        }

        // 3. S3에서 이미지 파일들 삭제
        try {
            if (display.getDisplayThumbnailUrl() != null) {
                s3Service.deleteS3(display.getDisplayThumbnailUrl());
            }

            for (DisplayImage image : display.getImages()) {
                if (image.getDisplayImgUrl() != null) {
                    s3Service.deleteS3(image.getDisplayImgUrl());
                }
            }
        } catch (Exception e) {
            log.error("S3 파일 삭제 중 오류 발생: ", e);
            throw new RuntimeException("S3 파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 4. 디스플레이 삭제
        try {
            displayRepository.delete(display);
            eventPublisher.publishEvent(new DisplayEvent("DELETE", display));
            log.info("디스플레이 삭제 완료: 디스플레이 ID {}", displayUid);
        } catch (Exception e) {
            log.error("디스플레이 삭제 중 오류 발생: ", e);
            throw new RuntimeException("디스플레이 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse downloadDisplay(String userId, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));
    
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    
        // 2. 이미 이 회원이 이 display를 저장했는지 확인
        if (displayStorageRepository.existsByUserAndDisplay(user, display)) {
            log.error("디스플레이 다운로드 실패: 이미 저장된 디스플레이 - 사용자 ID {} 디스플레이 ID {}", userId, displayUid);
            throw new IllegalStateException("이미 저장한 디스플레이입니다.");
        }
    
        // displayStorage 생성
        // 3. displayStorage 생성 및 저장
        DisplayStorage displayStorage = DisplayStorage.builder()
                .user(user)
                .display(display)
                .downloadAt(LocalDateTime.now())
                .isFavorites(false)
                .favoritesAt(null)
                .build();
    
        displayStorageRepository.save(displayStorage);
    
        // 4. Display의 Display_download_count 횟수 +1
        display.setDisplayDownloadCount(display.getDisplayDownloadCount() + 1);
        displayRepository.save(display);
    
        log.info("디스플레이 다운로드 성공: 사용자 ID {} 디스플레이 ID {}", userId, displayUid);
    
        // 응답 객체 생성 및 반환
        return DisplayCreateResponse.builder()
                .displayUid(display.getDisplayUid())
                .displayName(display.getDisplayName())
                .message("디스플레이가 성공적으로 다운로드되었습니다.")
                .build();
    }
    
    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse deleteStoredDisplay(String userId, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));
    
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
    
        // 2. 생성자인 경우 deleteDisplay 호출
        if (display.getCreatorUid().equals(user.getUserUid())) {
            deleteDisplay(displayUid, userId);
            return DisplayCreateResponse.builder()
                    .displayUid(display.getDisplayUid())
                    .displayName(display.getDisplayName())
                    .message("디스플레이가 완전히 삭제되었습니다.")
                    .build();
        }
    
        // 3. 일반 사용자의 경우 저장소에서만 삭제
        if (!displayStorageRepository.existsByUserAndDisplay(user, display)) {
            log.error("저장된 디스플레이 삭제 실패: 저장되지 않은 디스플레이 - 사용자 ID {} 디스플레이 ID {}", userId, displayUid);
            throw new IllegalStateException("저장된 디스플레이를 찾을 수 없습니다.");
        }
    
        DisplayStorage storedDisplay = displayStorageRepository.findByUserAndDisplay(user, display)
                .orElseThrow(() -> new EntityNotFoundException("저장한 디스플레이가 아닙니다."));
        displayStorageRepository.delete(storedDisplay);
    
        displayRepository.save(display);
    
        log.info("저장된 디스플레이 삭제 성공: 사용자 ID {} 디스플레이 ID {}", userId, displayUid);
    
        return DisplayCreateResponse.builder()
                .displayUid(display.getDisplayUid())
                .displayName(display.getDisplayName())
                .message("저장소에서 디스플레이가 삭제되었습니다.")
                .build();
    }

    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public DisplayCreateResponse updateDisplayFavorite(String userId, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2-1. 내가 제작한 디스플레이인지 확인
        boolean isCreator = display.getCreatorUid().equals(user.getUserUid());
        // 2-2. 내가 저장한 디스플레이인지 확인
        Optional<DisplayStorage> optionalStoragedDisplay = displayStorageRepository.findByUserAndDisplay(user, display);

        // 3. 내가 제작한 디스플레이이거나 저장한 디스플레이라면 상태 업데이트 진행
        if (isCreator || optionalStoragedDisplay.isPresent()) {
            DisplayStorage storagedDisplay = optionalStoragedDisplay.orElseThrow(
                    () -> new EntityNotFoundException("저장된 디스플레이를 찾을 수 없습니다.")
            );

            // 4. 현재 상태의 반대로 변경
            boolean newFavoriteStatus = !storagedDisplay.getIsFavorites();
            storagedDisplay.setIsFavorites(newFavoriteStatus);

            // 4-1. false -> true로 변경될 때만 시간 업데이트
            if (newFavoriteStatus) {
                storagedDisplay.setFavoritesAt(LocalDateTime.now());
            }

            // 5. 저장
            displayStorageRepository.save(storagedDisplay);

            // 응답 객체 생성 및 반환
            return DisplayCreateResponse.builder()
                    .displayUid(display.getDisplayUid())
                    .displayName(display.getDisplayName())
                    .message("디스플레이의 즐겨찾기 정보를 토글합니다.")
                    .build();

        } else {
            throw new IllegalStateException("해당 디스플레이에 대한 권한이 없습니다.");
        }
    }

    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public void doLikeDisplay(String userId, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 이미 이 회원이 이 display를 좋아요 했는지
        if (displayLikeRepository.existsByUserAndDisplay(user, display)) {
            throw new IllegalStateException("이미 좋아요를 누른 디스플레이입니다.");
        }

        // DisplayLike 생성
        // 3. DisplayLike 생성 및 저장
        DisplayLike displayLike = DisplayLike.builder()
                .user(user)
                .display(display)
                .likeCreatedAt(LocalDateTime.now())
                .build();
        displayLikeRepository.save(displayLike);

        // 4. Display의 Display_like_count 횟수 +1
        display.setDisplayLikeCount(display.getDisplayLikeCount() + 1);
        displayRepository.save(display);
        log.info("디스플레이 좋아요: 디스플레이 ID {}", displayUid);
    }

    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public void cancelLikeDisplay(String userId, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 저장된 디스플레이가 존재하는지 확인
        if (!displayLikeRepository.existsByUserAndDisplay(user, display)) {
            throw new IllegalStateException("좋아요한 디스플레이가 아닙니다.");
        }

        // 3. displayLike 삭제
        DisplayLike displayLike = displayLikeRepository.findByUserAndDisplay(user, display)
                .orElseThrow(() -> new IllegalStateException("좋아요한 디스플레이가 아닙니다."));
        displayLikeRepository.delete(displayLike);

        // 4. Display의 Display_like_count 횟수 -1
        display.setDisplayLikeCount(display.getDisplayLikeCount() - 1);
        displayRepository.save(display);
        log.info("디스플레이 좋아요 취소: 디스플레이 ID {}", displayUid);
    }

    @Override
    public DisplayLikedListResponse getLikedDisplayList(String userId, Pageable pageable) {
        try {
            // 사용자 id를 통해 userUid 조회
            Optional<User> userOptional = userRepository.findByUserId(userId);
            User user = userOptional.get();

            // 내 좋아요 디스플레이 목록 조회
            Page<DisplayLike> likedDisplays = displayLikeRepository.findAllByUser(user, pageable);
            List<DisplayLikedListResponse.DisplayInfo> displayInfos = likedDisplays.getContent().stream()
                    .map(displayLike -> DisplayLikedListResponse.DisplayInfo.builder()
                            .displayUid(displayLike.getDisplay().getDisplayUid())
                            .displayThumbnail(displayLike.getDisplay().getDisplayThumbnailUrl())
                            .build())
                    .collect(Collectors.toList());

            return DisplayLikedListResponse.builder()
                    .currentPage(likedDisplays.getNumber())
                    .displays(displayInfos)
                    .build();

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("사용자를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("내가 좋아요한 디스플레이 목록 조회 중 오류가 발생했습니다.");
        }
    }


    // 해당 display에 있는 댓글 전체 조회
    @Override
    public List<DisplayCommentResponse> getComments(String userId, long displayUid) {
        // 해당 display찾기
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User currentUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 부모 댓글들 다 찾기
        List<DisplayComment> comments = displayCommentRepository
                .findByDisplayAndParentCommentIsNullOrderByCommentCreatedAt(display);
        
        log.info("디스플레이 댓글 조회: 디스플레이 ID {}", displayUid);
        return comments.stream()
                .map(comment -> DisplayCommentResponse.convertToDTO(comment, currentUser))
                .collect(Collectors.toList());
    }

    // display에 댓글 작성
    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public void createComment(String userId, Long displayId, DisplayCommentRequest requestDTO) {
        // 해당 display찾기
        Display display = displayRepository.findById(displayId)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 댓글 객체 생성
        DisplayComment comment = DisplayComment.builder()
                .display(display)
                .user(user)
                .commentText(requestDTO.getCommentText())
                .commentCreatedAt(LocalDateTime.now())
                .commentUpdatedAt(LocalDateTime.now())
                .build();

        log.info("디스플레이 댓글 작성: 디스플레이 ID {} 댓글 ID {}", displayId, comment.getCommentUid());
        // 대댓글인 경우 - parentComment도 추가
        if (requestDTO.getParentCommentUid() != null) {
            DisplayComment parentComment = displayCommentRepository.findById(requestDTO.getParentCommentUid())
                    .orElseThrow(() -> new IllegalStateException("부모 댓글을 찾을 수 없습니다."));
            comment.setParentComment(parentComment);
        }

        displayCommentRepository.save(comment);
    }

    // 내 댓글 수정
    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public void updateComment(String userId, Long displayId, DisplayCommentUpdateRequest request) {
        // display찾기
        Display display = displayRepository.findById(displayId)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // comment찾기
        DisplayComment comment = displayCommentRepository.findById(request.getCommentUid())
                .orElseThrow(() -> new IllegalStateException("댓글을 찾을 수 없습니다."));

        // display에 달린 댓글인지 확인
        if (!comment.getDisplay().equals(display)) {
            throw new IllegalArgumentException("해당 디스플레이의 댓글이 아닙니다.");
        }

        // 현재 유저가 작성한 댓글인지 확인
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("자신의 댓글만 수정할 수 있습니다.");
        }

        comment.setCommentText(request.getNewCommentText());
        comment.setCommentUpdatedAt(LocalDateTime.now());
        log.info("디스플레이 댓글 수정: 디스플레이 ID {} 댓글 ID {}", displayId, comment.getCommentUid());
        displayCommentRepository.save(comment);
    }

    // 내 댓글 삭제
    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public void deleteComment(String userId, Long displayUid, Long commentUid) {
        // display찾기
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // comment찾기
        DisplayComment comment = displayCommentRepository.findById(commentUid)
                .orElseThrow(() -> new IllegalStateException("댓글을 찾을 수 없습니다."));

        // display에 달린 댓글인지 확인
        if (!comment.getDisplay().equals(display)) {
            throw new IllegalArgumentException("해당 디스플레이의 댓글이 아닙니다.");
        }

        // 현재 유저가 작성한 댓글인지 확인
        if (!comment.getUser().equals(user)) {
            throw new IllegalArgumentException("자신의 댓글만 삭제할 수 있습니다.");
        }

        // 삭제
        displayCommentRepository.deleteById(commentUid);
        log.info("디스플레이 댓글 삭제: 디스플레이 ID {} 댓글 ID {}", displayUid, commentUid);
    }

    @Override
    @CacheEvict(value = {"allDisplays", "myDisplays", "displayDetails"}, allEntries = true)
    public DisplayPostedToggleResponse updateDisplayStatus(Long displayUid, String userId) {
        // 사용자 검증
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 디스플레이 조회
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 디스플레이입니다."));

        // 권한 검증 (생성자만 수정 가능)
        if (!display.getCreatorUid().equals(user.getUserUid())) {
            throw new IllegalStateException("디스플레이 수정 권한이 없습니다.");
        }

        // 상태 업데이트
        display.setDisplayIsPosted(!display.getDisplayIsPosted());
        displayRepository.save(display);  // 변경사항 저장
        log.info("디스플레이 상태 업데이트: 디스플레이 ID {} 상태 {}", displayUid, display.getDisplayIsPosted());

        return DisplayPostedToggleResponse.builder()
                .displayUid(display.getDisplayUid())
                .displayIsPosted(display.getDisplayIsPosted())
                .build();
    }

}



