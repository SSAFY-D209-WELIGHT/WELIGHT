package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.DisplayBackgroundDto;
import com.d209.welight.domain.display.dto.DisplayImageDto;
import com.d209.welight.domain.display.dto.DisplayTextDto;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.*;
import com.d209.welight.domain.display.entity.*;
import com.d209.welight.domain.display.entity.displaylike.DisplayLike;
import com.d209.welight.domain.display.entity.displaystorage.DisplayStorage;
import com.d209.welight.domain.elasticsearch.event.DisplayEvent;
import com.d209.welight.domain.display.repository.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.global.exception.display.DisplayNotFoundException;
import com.d209.welight.global.exception.display.InvalidDisplayDataException;
import com.d209.welight.global.service.s3.S3Service;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    @Transactional
    public DisplayCreateResponse createDisplay(String userId, DisplayCreateRequest request) {

        validateDisplayCreateRequest(request);

        try {
            Optional<User> user = userRepository.findByUserId(userId);
            // 1. Display 엔티티 생성
            Display display = Display.builder()
                .creatorUid(user.get().getUserUid())
                .displayName(request.getDisplayName())
                .displayThumbnailUrl(request.getDisplayThumbnailUrl())
                .displayIsPosted(request.getDisplayIsPosted())  // 초기 생성시 게시되지 않은 상태
                .displayCreatedAt(LocalDateTime.now())
                .displayDownloadCount(0L)
                .displayLikeCount(0L)
                .build();

            // 1. 디스플레이 기본 정보 저장
            Display savedDisplay = displayRepository.save(display);


            // 이벤트 발행
            eventPublisher.publishEvent(new DisplayEvent("CREATE", display));

            // 2. 태그 정보 저장
            if (request.getTags() != null && !request.getTags().isEmpty()) {
                List<DisplayTag> tags = request.getTags().stream()
                    .map(tag -> {
                        DisplayTag displayTag = new DisplayTag();
                        displayTag.setDisplay(savedDisplay);
                        displayTag.setDisplayTagText(tag);                      // 태그 텍스트 설정
                        displayTag.setDisplayTagCreatedAt(LocalDateTime.now());    // 생성 시간 설정
                        return displayTag;
                    })
                    .collect(Collectors.toList());
                displayTagRepository.saveAll(tags);
            }

            // 3. 이미지 정보 저장
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                List<DisplayImage> images = request.getImages().stream()
                    .map(imageDto -> {
                        DisplayImage image = new DisplayImage();
                        image.setDisplay(savedDisplay);
                        image.setDisplayImgUrl(imageDto.getDisplayImgUrl());
                        image.setDisplayImgColor(imageDto.getDisplayImgColor());
                        image.setDisplayImgScale(imageDto.getDisplayImgScale());
                        image.setDisplayImgRotation(imageDto.getDisplayImgRotation());
                        image.setDisplayImgOffsetx(imageDto.getDisplayImgOffsetx());
                        image.setDisplayImgOffsety(imageDto.getDisplayImgOffsety());
                        image.setDisplayImgCreatedAt(LocalDateTime.now());
                        return image;
                    })
                    .collect(Collectors.toList());

                displayImageRepository.saveAll(images);
                savedDisplay.setImages(images);
            }

            // 4. 텍스트 정보 저장
            if (request.getTexts() != null && !request.getTexts().isEmpty()) {
                List<DisplayText> texts = request.getTexts().stream()
                    .map(textDto -> {
                        DisplayText text = new DisplayText();
                        text.setDisplay(savedDisplay);
                        text.setDisplayTextDetail(textDto.getDisplayTextDetail());
                        text.setDisplayTextColor(textDto.getDisplayTextColor());
                        text.setDisplayTextFont(textDto.getDisplayTextFont());
                        text.setDisplayTextRotation(textDto.getDisplayTextRotation());
                        text.setDisplayTextScale(textDto.getDisplayTextScale());
                        text.setDisplayTextOffsetx(textDto.getDisplayTextOffsetx());
                        text.setDisplayTextOffsety(textDto.getDisplayTextOffsety());
                        text.setDisplayTextCreatedAt(LocalDateTime.now());
                        return text;
                    })
                    .collect(Collectors.toList());

                displayTextRepository.saveAll(texts);
                savedDisplay.setTexts(texts);
            }

            // 5. 배경 정보 저장
            if (request.getBackground() != null) {
                DisplayBackground background = new DisplayBackground();
                background.setDisplay(savedDisplay);
                background.setDisplayBackgroundBrightness(request.getBackground().getDisplayBackgroundBrightness());
                background.setDisplayColorSolid(request.getBackground().getDisplayColorSolid());
                background.setDisplayBackgroundGradationColor1(request.getBackground().getDisplayBackgroundGradationColor1());
                background.setDisplayBackgroundGradationColor2(request.getBackground().getDisplayBackgroundGradationColor2());
                background.setDisplayBackgroundGradationType(request.getBackground().getDisplayBackgroundGradationType());
                background.setDisplayBackgroundCreatedAt(LocalDateTime.now());

                displayBackgroundRepository.save(background);
                savedDisplay.setBackground(background);

            }

            DisplayStorage displayStorage = DisplayStorage.builder()
                    .user(user.get())
                    .display(display)
                    .downloadAt(LocalDateTime.now())
                    .isFavorites(false)
                    .build();
            displayStorageRepository.save(displayStorage);

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

    @Override
    public DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request) {

        // Display 존재 여부 확인
        Display display = displayRepository.findById(request.getDisplayUid())
                .orElseThrow(() -> new DisplayNotFoundException("디스플레이를 찾을 수 없습니다."));

        try {
            // 태그 정보 조회
            List<DisplayTag> tags = displayTagRepository.findByDisplay(display);

            // 현재 사용자의 userId를 userUid로 변환하여 비교
            boolean isOwner = false;
            boolean isFavorite = false;

            if (request.getUserId() != null) {
                Optional<User> currentUser = userRepository.findByUserId(request.getUserId());
                if (currentUser.isPresent()) {
                    isOwner = display.getCreatorUid().equals(currentUser.get().getUserUid());

                    // 즐겨찾기 여부 확인
                    isFavorite = displayStorageRepository.existsByUserAndDisplay(currentUser, display);
                }
            }

            // Response 객체 생성 및 반환
            return DisplayDetailResponse.builder()
                    .creatorUid(display.getCreatorUid())
                    .displayName(display.getDisplayName())
                    .displayThumbnailUrl(display.getDisplayThumbnailUrl())
                    .displayIsPosted(display.getDisplayIsPosted())
                    .tags(tags.stream()
                            .map(DisplayTag::getDisplayTagText)
                            .collect(Collectors.toList()))
                    .isOwner(isOwner)
                    .isFavourite(isFavorite)
                    .likeCount(display.getDisplayLikeCount())
                    .downloadCount(display.getDisplayDownloadCount())
                    .commentCount(displayCommentRepository.countByDisplay(display))
                    .build();

        } catch (Exception e) {
            log.error("디스플레이 상세 조회 중 예외 발생", e);
            throw e;
        }
    }

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

            return DisplayListResponse.builder()
                .currentPage(pageable.getPageNumber())
                .displays(displayInfos)
                .build();
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 목록 조회 중 오류가 발생했습니다.");
        }
    }

    @Override
    public DisplayListResponse getMyDisplayList(String userId, Pageable pageable) {
        try {
            // 사용자 id를 통해 userUid 조회
            Optional<User> user = userRepository.findByUserId(userId);
            Long userUid = user.get().getUserUid();

            // 내가 생성한 디스플레이와 저장한 디스플레이 모두 조회
            Page<Display> displays = displayRepository.findAllByCreatorUidOrStoredByUser(userUid, user, pageable);

            // 사용자의 디스플레이 목록 조회
            List<DisplayListResponse.DisplayInfo> displayInfos = displays.getContent().stream()
                    .map(display -> {
                        // DisplayStorage에서 즐겨찾기 여부 확인
                        boolean isFavorite = displayStorageRepository.findByUserAndDisplay(user.get(), display)
                                .map(DisplayStorage::getIsFavorites)
                                .orElse(false);

                        return DisplayListResponse.DisplayInfo.builder()
                                .displayUid(display.getDisplayUid())
                                .displayThumbnail(display.getDisplayThumbnailUrl())
                                .isFavorite(isFavorite)  // 즐겨찾기 여부 추가
                                .build();
                    })
                    .collect(Collectors.toList());

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
    public DisplayCreateResponse duplicateDisplay(Long displayId, String userId) {
        log.info("디스플레이 복제 시작 - displayId: {}, userId: {}", displayId, userId);

        try {
            // 1. 사용자 조회
            log.debug("사용자 조회 시도 - userId: {}", userId);
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없음 - userId: {}", userId);
                        return new EntityNotFoundException("사용자를 찾을 수 없습니다.");
                    });
            log.debug("사용자 조회 성공 - userUid: {}", user.getUserUid());

            // 2. 원본 디스플레이 조회
            log.debug("원본 디스플레이 조회 시도 - displayId: {}", displayId);
            Display originalDisplay = displayRepository.findById(displayId)
                    .orElseThrow(() -> {
                        log.error("디스플레이를 찾을 수 없음 - displayId: {}", displayId);
                        return new EntityNotFoundException("디스플레이를 찾을 수 없습니다.");
                    });
            log.debug("원본 디스플레이 조회 성공");

            // 3. 새로운 디스플레이 생성
            log.debug("새로운 디스플레이 생성 시작");
            Display newDisplay = Display.builder()
                    .creatorUid(user.getUserUid())
                    .displayName(originalDisplay.getDisplayName() + "_복제")
                    .displayIsPosted(false)
                    .displayCreatedAt(LocalDateTime.now())
                    .displayDownloadCount(0L)
                    .displayLikeCount(0L)
                    .displayThumbnailUrl(originalDisplay.getDisplayThumbnailUrl())
                    .build();

            // 4. 썸네일 복제
            if (originalDisplay.getDisplayThumbnailUrl() != null && !originalDisplay.getDisplayThumbnailUrl().isEmpty()) {
                try {
                    log.debug("썸네일 복제 시도");
                    String newThumbnailName = generateFileName(userId, "thumbnails", originalDisplay.getDisplayThumbnailUrl());
                    String newThumbnailUrl = s3Service.copyS3(originalDisplay.getDisplayThumbnailUrl(), newThumbnailName);
                    newDisplay.setDisplayThumbnailUrl(newThumbnailUrl);
                    log.debug("썸네일 복제 성공 - newUrl: {}", newThumbnailUrl);
                } catch (Exception e) {
                    log.warn("썸네일 복제 실패 - 기존 URL 유지: {}", e.getMessage());
                }
            }

            // 5. 디스플레이 저장
            log.debug("새로운 디스플레이 저장 시도");
            Display savedDisplay = displayRepository.save(newDisplay);
            log.debug("새로운 디스플레이 저장 성공 - displayUid: {}", savedDisplay.getDisplayUid());

            // 6. 이벤트 발행
            log.debug("디스플레이 생성 이벤트 발행");
            eventPublisher.publishEvent(new DisplayEvent("CREATE", savedDisplay));

            // 7. 연관 데이터 복제
            try {
                log.debug("연관 데이터 복제 시작");
                duplicateBackground(originalDisplay.getBackground(), savedDisplay);
                duplicateTexts(originalDisplay.getTexts(), savedDisplay);
                duplicateImages(originalDisplay.getImages(), savedDisplay, userId);
                log.debug("연관 데이터 복제 완료");
            } catch (Exception e) {
                log.error("연관 데이터 복제 실패: ", e);
                throw new RuntimeException("연관 데이터 복제 중 오류가 발생했습니다: " + e.getMessage());
            }

            // 8. 저장소 생성
            try {
                log.debug("저장소 생성 시도");
                DisplayStorage displayStorage = DisplayStorage.builder()
                        .user(user)
                        .display(savedDisplay)
                        .downloadAt(LocalDateTime.now())
                        .isFavorites(false)
                        .favoritesAt(null)
                        .build();
                displayStorageRepository.save(displayStorage);
                log.debug("저장소 생성 성공");
            } catch (Exception e) {
                log.error("저장소 생성 실패: ", e);
                throw new RuntimeException("저장소 생성 중 오류가 발생했습니다: " + e.getMessage());
            }

            log.info("디스플레이 복제 완료 - newDisplayUid: {}", savedDisplay.getDisplayUid());
            return DisplayCreateResponse.builder()
                    .displayUid(savedDisplay.getDisplayUid())
                    .displayName(savedDisplay.getDisplayName())
                    .message("디스플레이가 성공적으로 복제되었습니다.")
                    .build();

        } catch (EntityNotFoundException e) {
            log.error("엔티티 조회 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("디스플레이 복제 중 예기치 않은 오류: ", e);
            throw new RuntimeException("디스플레이 복제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void duplicateTexts(List<DisplayText> originalTexts, Display newDisplay) {
        try {
             //  디스플레이 정보 수정 후 나머지 텍스트 정보 복제
            if (originalTexts != null) {
                originalTexts.forEach(text -> {
                    DisplayText newText = DisplayText.builder()
                            .display(newDisplay)
                            .displayTextDetail(text.getDisplayTextDetail())
                            .displayTextColor(text.getDisplayTextColor())
                            .displayTextFont(text.getDisplayTextFont())
                            .displayTextRotation(text.getDisplayTextRotation())
                            .displayTextScale(text.getDisplayTextScale())
                            .displayTextOffsetx(text.getDisplayTextOffsetx())
                            .displayTextOffsety(text.getDisplayTextOffsety())
                            .displayTextCreatedAt(LocalDateTime.now())
                            .build();

                    // 디스플레이 텍스트 저장
                    displayTextRepository.save(newText);
                });
            }
            
        } catch (Exception e) {
            throw new RuntimeException("텍스트 복제 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public void duplicateImages(List<DisplayImage> originalImages, Display newDisplay, String userId) {
        try {
            if (originalImages != null) {
                for (DisplayImage image : originalImages) {
                    try {
                        String originalImgUrl = image.getDisplayImgUrl();
                        if (originalImgUrl != null && !originalImgUrl.isEmpty()) {
                            // 새로운 파일명 생성
                            String newFileName = generateFileName(userId, "images", originalImgUrl);
    
                            // S3에 이미지 복사
                            String newImgUrl = s3Service.copyS3(originalImgUrl, newFileName);
    
                            // 새로운 이미지 엔티티 생성 및 저장
                            DisplayImage newImage = DisplayImage.builder()
                                .display(newDisplay)
                                .displayImgUrl(newImgUrl)
                                .displayImgColor(image.getDisplayImgColor())
                                .displayImgScale(image.getDisplayImgScale())
                                .displayImgRotation(image.getDisplayImgRotation())
                                .displayImgOffsetx(image.getDisplayImgOffsetx())
                                .displayImgOffsety(image.getDisplayImgOffsety())
                                .displayImgCreatedAt(LocalDateTime.now())
                                .build();
    
                            displayImageRepository.save(newImage);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("이미지 복제 중 오류가 발생했습니다: " + e.getMessage());
                    }
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("이미지 복제 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public void duplicateBackground(DisplayBackground originalBackground, Display newDisplay) {
        
        try {
            if (originalBackground != null) {

                // 새로운 배경 엔티티 생성
                DisplayBackground newBackground = DisplayBackground.builder()
                        .display(newDisplay)
                        .displayBackgroundBrightness(originalBackground.getDisplayBackgroundBrightness())
                        .displayColorSolid(originalBackground.getDisplayColorSolid())
                        .displayBackgroundGradationColor1(originalBackground.getDisplayBackgroundGradationColor1())
                        .displayBackgroundGradationColor2(originalBackground.getDisplayBackgroundGradationColor2())
                        .displayBackgroundGradationType(originalBackground.getDisplayBackgroundGradationType())
                        .build();
    
                // 디스플레이 배경 저장
                displayBackgroundRepository.save(newBackground);
            }
        } catch (Exception e) {
            throw new RuntimeException("배경 복제 중 오류가 발생했습니다.");
        }
       
    }

    @Override
    public DisplayCreateRequest getDisplayForEdit(Long displayId, String userId) {
        try {
            // 권한 확인
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            Display display = displayRepository.findById(displayId)
                    .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

            if (!display.getCreatorUid().equals(user.getUserUid())) {
                throw new IllegalStateException("디스플레이를 수정할 권한이 없습니다.");
            }

            // 태그 정보 변환
            List<String> tags = display.getTags().stream()
                    .map(DisplayTag::getDisplayTagText)
                    .collect(Collectors.toList());

            // 이미지 정보 변환
            List<DisplayImageDto> images = display.getImages().stream()
                    .map(image -> DisplayImageDto.builder()
                            .displayImgUrl(image.getDisplayImgUrl())
                            .displayImgColor(image.getDisplayImgColor())
                            .displayImgScale(image.getDisplayImgScale())
                            .displayImgRotation(image.getDisplayImgRotation())
                            .displayImgOffsetx(image.getDisplayImgOffsetx())
                            .displayImgOffsety(image.getDisplayImgOffsety())
                            .build())
                    .collect(Collectors.toList());

            // 텍스트 정보 변환
            List<DisplayTextDto> texts = display.getTexts().stream()
                    .map(text -> DisplayTextDto.builder()
                            .displayTextDetail(text.getDisplayTextDetail())
                            .displayTextColor(text.getDisplayTextColor())
                            .displayTextFont(text.getDisplayTextFont())
                            .displayTextRotation(text.getDisplayTextRotation())
                            .displayTextScale(text.getDisplayTextScale())
                            .displayTextOffsetx(text.getDisplayTextOffsetx())
                            .displayTextOffsety(text.getDisplayTextOffsety())
                            .build())
                    .collect(Collectors.toList());


            DisplayBackgroundDto background = DisplayBackgroundDto.builder()
                    .displayBackgroundBrightness(display.getBackground().getDisplayBackgroundBrightness())
                    .displayColorSolid(display.getBackground().getDisplayColorSolid())
                    .displayBackgroundGradationColor1(display.getBackground().getDisplayBackgroundGradationColor1())
                    .displayBackgroundGradationColor2(display.getBackground().getDisplayBackgroundGradationColor2())
                    .displayBackgroundGradationType(display.getBackground().getDisplayBackgroundGradationType())
                    .build();

            // DisplayCreateRequest 생성 및 반환
            return DisplayCreateRequest.builder()
                    .displayName(display.getDisplayName())
                    .displayThumbnailUrl(display.getDisplayThumbnailUrl())
                    .displayIsPosted(display.getDisplayIsPosted())
                    .tags(tags)
                    .images(images)
                    .texts(texts)
                    .background(background)
                    .build();

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("디스플레이를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 수정 정보 조회 중 오류가 발생했습니다.");
        }
    }

    @Override
    public DisplayCreateResponse updateDisplay(Long displayId, DisplayCreateRequest request, String userId) {
        try {
            // 1. 권한 확인
            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

            Display originalDisplay = displayRepository.findById(displayId)
                    .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

            if (!originalDisplay.getCreatorUid().equals(user.getUserUid())) {
                throw new IllegalStateException("디스플레이를 수정할 권한이 없습니다.");
            }

            // 2. 새로운 디스플레이 생성 (기존 정보 복사)
            Display newDisplay = Display.builder()
                    .creatorUid(user.getUserUid())
                    .displayName(request.getDisplayName() != null ? request.getDisplayName() : originalDisplay.getDisplayName())
                    .displayThumbnailUrl(request.getDisplayThumbnailUrl() != null ? request.getDisplayThumbnailUrl() : originalDisplay.getDisplayThumbnailUrl())
                    .displayIsPosted(request.getDisplayIsPosted() != null ? request.getDisplayIsPosted() : originalDisplay.getDisplayIsPosted())
                    .displayCreatedAt(LocalDateTime.now())
                    .displayDownloadCount(0L)
                    .displayLikeCount(0L)
                    .build();
            
            // 수정된(새로운) 디스플레이 저장
            Display savedDisplay = displayRepository.save(newDisplay);

            eventPublisher.publishEvent(new DisplayEvent("CREATE",savedDisplay));

            // 3. 컨텐츠 복사 또는 업데이트
            // 3-1. 태그 처리
            List<String> newTags = request.getTags() != null ? request.getTags() :
                    originalDisplay.getTags().stream()
                            .map(DisplayTag::getDisplayTagText)
                            .toList();

            List<DisplayTag> tags = newTags.stream()
                    .map(tag -> DisplayTag.builder()
                            .display(savedDisplay)
                            .displayTagText(tag)
                            .displayTagCreatedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            // 수정된 태그 저장
            displayTagRepository.saveAll(tags);

            // 3-2. 이미지 처리
            List<DisplayImage> images;
            if (request.getImages() != null) {
                images = request.getImages().stream()
                        .map(imageDto -> DisplayImage.builder()
                                .display(savedDisplay)
                                .displayImgUrl(imageDto.getDisplayImgUrl())
                                .displayImgColor(imageDto.getDisplayImgColor())
                                .displayImgScale(imageDto.getDisplayImgScale())
                                .displayImgRotation(imageDto.getDisplayImgRotation())
                                .displayImgOffsetx(imageDto.getDisplayImgOffsetx())
                                .displayImgOffsety(imageDto.getDisplayImgOffsety())
                                .displayImgCreatedAt(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            } else {
                images = originalDisplay.getImages().stream()
                        .map(image -> DisplayImage.builder()
                                .display(savedDisplay)
                                .displayImgUrl(image.getDisplayImgUrl())
                                .displayImgColor(image.getDisplayImgColor())
                                .displayImgScale(image.getDisplayImgScale())
                                .displayImgRotation(image.getDisplayImgRotation())
                                .displayImgOffsetx(image.getDisplayImgOffsetx())
                                .displayImgOffsety(image.getDisplayImgOffsety())
                                .displayImgCreatedAt(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }

            // 수정된 이미지 저장 
            displayImageRepository.saveAll(images);

            // 3-3. 텍스트 처리
            List<DisplayText> texts;
            if (request.getTexts() != null) {
                texts = request.getTexts().stream()
                        .map(textDto -> DisplayText.builder()
                                .display(savedDisplay)
                                .displayTextDetail(textDto.getDisplayTextDetail())
                                .displayTextColor(textDto.getDisplayTextColor())
                                .displayTextFont(textDto.getDisplayTextFont())
                                .displayTextRotation(textDto.getDisplayTextRotation())
                                .displayTextScale(textDto.getDisplayTextScale())
                                .displayTextOffsetx(textDto.getDisplayTextOffsetx())
                                .displayTextOffsety(textDto.getDisplayTextOffsety())
                                .displayTextCreatedAt(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            } else {
                texts = originalDisplay.getTexts().stream()
                        .map(text -> DisplayText.builder()
                                .display(savedDisplay)
                                .displayTextDetail(text.getDisplayTextDetail())
                                .displayTextColor(text.getDisplayTextColor())
                                .displayTextFont(text.getDisplayTextFont())
                                .displayTextRotation(text.getDisplayTextRotation())
                                .displayTextScale(text.getDisplayTextScale())
                                .displayTextOffsetx(text.getDisplayTextOffsetx())
                                .displayTextOffsety(text.getDisplayTextOffsety())
                                .displayTextCreatedAt(LocalDateTime.now())
                                .build())
                        .collect(Collectors.toList());
            }
            
            // 수정된 텍스트 저장
            displayTextRepository.saveAll(texts);

            // 3-4. 배경 및 색상 처리
            DisplayBackground newBackground;
            if (request.getBackground() != null) {
                newBackground = DisplayBackground.builder()
                        .display(savedDisplay)
                        .displayBackgroundBrightness(request.getBackground().getDisplayBackgroundBrightness())
                        .displayColorSolid(request.getBackground().getDisplayColorSolid())
                        .displayBackgroundGradationColor1(request.getBackground().getDisplayBackgroundGradationColor1())
                        .displayBackgroundGradationColor2(request.getBackground().getDisplayBackgroundGradationColor2())
                        .displayBackgroundGradationType(request.getBackground().getDisplayBackgroundGradationType())
                        .displayBackgroundCreatedAt(LocalDateTime.now())
                        .build();
            } else {
                DisplayBackground originalBackground = originalDisplay.getBackground();
                newBackground = DisplayBackground.builder()
                        .display(savedDisplay)
                        .displayBackgroundBrightness(originalBackground.getDisplayBackgroundBrightness())
                        .displayColorSolid(originalBackground.getDisplayColorSolid())
                        .displayBackgroundGradationColor1(originalBackground.getDisplayBackgroundGradationColor1())
                        .displayBackgroundGradationColor2(originalBackground.getDisplayBackgroundGradationColor2())
                        .displayBackgroundGradationType(originalBackground.getDisplayBackgroundGradationType())
                        .displayBackgroundCreatedAt(LocalDateTime.now())
                        .build();
            }
            
            // 수정된 배경 저장
            displayBackgroundRepository.save(newBackground);


            return DisplayCreateResponse.builder()
                    .displayUid(savedDisplay.getDisplayUid())
                    .displayName(savedDisplay.getDisplayName())
                    .message("디스플레이가 성공적으로 수정되었습니다.")
                    .build();

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("디스플레이를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 수정 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public void deleteDisplay(Long displayUid, String userId) {
        // 1. Display와 User 정보 확인
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 권한 확인 (생성자 또는 관리자만 삭제 가능)
        if (!display.getCreatorUid().equals(user.getUserUid()) && !user.isUserIsAdmin()) {
            throw new IllegalStateException("디스플레이를 삭제할 권한이 없습니다.");
        }

        try {
            // 3. S3에서 이미지 파일들 삭제
            // 썸네일 삭제
            if (display.getDisplayThumbnailUrl() != null) {
                s3Service.deleteS3(display.getDisplayThumbnailUrl());
            }

            // 디스플레이 이미지들 삭제
            display.getImages().forEach(image -> {
                if (image.getDisplayImgUrl() != null) {
                    try {
                        s3Service.deleteS3(image.getDisplayImgUrl());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // 4. 연관된 데이터 삭제
            // 좋아요 삭제
            displayLikeRepository.deleteByUserAndDisplay(user, display);

            // 저장소 삭제
            displayStorageRepository.deleteByUserAndDisplay(user, display);

            // 태그 삭제
            displayTagRepository.deleteByDisplay(display);

            // 이미지 삭제
            displayImageRepository.deleteByDisplay(display);

            // 텍스트 삭제
            displayTextRepository.deleteByDisplay(display);

            // 배경 색상 삭제
            displayBackgroundRepository.deleteByDisplay(display);

            // 5. 디스플레이 삭제
            displayRepository.delete(display);
            eventPublisher.publishEvent(new DisplayEvent("DELETE", display));

        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("디스플레이를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("디스플레이 삭제 중 오류가 발생했습니다.");
        }
    }


    private String generateFileName(String userId, String type, String originalFileUrl) {
        // 현재 timestamp를 이용해 고유성 보장
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 원본 파일명 추출 (URL의 마지막 '/' 이후 부분)
        String originalFileName = originalFileUrl.substring(originalFileUrl.lastIndexOf('/') + 1);

        // 확장자를 제외한 파일명 추출
        String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

        // 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        // 경로 구조: {userId}/{type}/{원본파일명}_{timestamp}{확장자}
        return String.format("%s/%s/%s_%s%s",
                userId,
                type,
                fileNameWithoutExtension,
                timestamp,
                extension);
    }

    /*
     * 디스플레이 저장소 (다운로드, 삭제)
     * */
    @Override
    public void downloadDisplay(User user, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // 2. 이미 이 회원이 이 display를 저장했는지 확인
        if (displayStorageRepository.existsByUserAndDisplay(user, display)) {
            throw new EntityExistsException("이미 저장한 디스플레이입니다.");
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

    }

    @Override
    public void deleteStoredDisplay(User user, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));
        // 2. 저장된 디스플레이가 존재하는지 확인
        if (!displayStorageRepository.existsByUserAndDisplay(user, display)) {
            throw new EntityNotFoundException("저장된 디스플레이를 찾을 수 없습니다.");
        }

        // 3. 저장소에서 삭제
        DisplayStorage storedDisplay = displayStorageRepository.findByUserAndDisplay(user, display)
                .orElseThrow(() -> new EntityNotFoundException("저장한 디스플레이가 아닙니다."));
        displayStorageRepository.delete(storedDisplay);

        // 4. Display의 Display_download_count 횟수 -1
        display.setDisplayDownloadCount(display.getDisplayDownloadCount() - 1);
        displayRepository.save(display);
    }

    @Override
    public void updateDisplayFavorite(User user, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));
        // 2. 저장된 디스플레이가 존재하는지 확인
        DisplayStorage storagedDisplay = displayStorageRepository.findByUserAndDisplay(user, display)
                .orElseThrow(() -> new EntityNotFoundException("저장된 디스플레이를 찾을 수 없습니다."));

        // 3. 현재 상태의 반대로 변경
        boolean newFavoriteStatus = !storagedDisplay.getIsFavorites();
        storagedDisplay.setIsFavorites(newFavoriteStatus);

        // 3-1. false -> true로 변경될 때만 시간 업데이트
        if (newFavoriteStatus) {
            storagedDisplay.setFavoritesAt(LocalDateTime.now());
        }

        displayStorageRepository.save(storagedDisplay);
    }

    @Override
    public void doLikeDisplay(User user, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // 2. 이미 이 회원이 이 display를 좋아요 했는지
        if (displayLikeRepository.existsByUserAndDisplay(user, display)) {
            throw new EntityExistsException("이미 좋아요를 누른 디스플레이입니다.");
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
    }

    public void cancelLikeDisplay(User user, long displayUid) {
        // 1. Display정보 불러오기 (Display 존재 여부 확인)
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));
        // 2. 저장된 디스플레이가 존재하는지 확인
        if (!displayLikeRepository.existsByUserAndDisplay(user, display)) {
            throw new EntityNotFoundException("좋아요한 디스플레이가 아닙니다.");
        }

        // 3. displayLike 삭제
        DisplayLike displayLike = displayLikeRepository.findByUserAndDisplay(user, display)
                .orElseThrow(() -> new EntityNotFoundException("좋아요한 디스플레이가 아닙니다."));
        displayLikeRepository.delete(displayLike);

        // 4. Display의 Display_like_count 횟수 -1
        display.setDisplayLikeCount(display.getDisplayLikeCount() - 1);
        displayRepository.save(display);
    }


    /*
     * 댓글
     * */

    // 해당 display에 있는 댓글 전체 조회
    @Override
    public List<DisplayCommentResponse> getComments(User currentUser, long displayUid) {
        // 해당 display찾기
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // 부모 댓글들 다 찾기
        List<DisplayComment> comments = displayCommentRepository
                .findByDisplayAndParentCommentIsNullOrderByCommentCreatedAt(display);

        return comments.stream()
                .map(comment -> DisplayCommentResponse.convertToDTO(comment, currentUser))
                .collect(Collectors.toList());
    }

    // display에 댓글 작성
    @Override
    public void createComment(User user, Long displayId, DisplayCommentRequest requestDTO) {
        // 해당 display찾기
        Display display = displayRepository.findById(displayId)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // 댓글 객체 생성
        DisplayComment comment = DisplayComment.builder()
                .display(display)
                .user(user)
                .commentText(requestDTO.getCommentText())
                .commentCreatedAt(LocalDateTime.now())
                .commentUpdatedAt(LocalDateTime.now())
                .build();

        // 대댓글인 경우 - parentComment도 추가
        if (requestDTO.getParentCommentUid() != null) {
            DisplayComment parentComment = displayCommentRepository.findById(requestDTO.getParentCommentUid())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
            comment.setParentComment(parentComment);
        }

        displayCommentRepository.save(comment);
    }

    // 내 댓글 수정
    @Override
    public void updateComment(User user, Long displayId, DisplayCommentUpdateRequest request) {
        // display찾기
        Display display = displayRepository.findById(displayId)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // comment찾기
        DisplayComment comment = displayCommentRepository.findById(request.getCommentUid())
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

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
        displayCommentRepository.save(comment);
    }

    // 내 댓글 삭제
    @Override
    public void deleteComment(User user, Long displayUid, Long commentUid) {
        // display찾기
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

        // comment찾기
        DisplayComment comment = displayCommentRepository.findById(commentUid)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));

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
    }

    @Override
    public DisplayPostedToggleResponse updateDisplayStatus(Long displayUid, String userId) {
        // 사용자 검증
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."));

        // 디스플레이 조회
        Display display = displayRepository.findById(displayUid)
                .orElseThrow(() -> new DisplayNotFoundException("존재하지 않는 디스플레이입니다."));

        // 권한 검증 (생성자만 수정 가능)
        if (!display.getCreatorUid().equals(user.getUserUid())) {
            throw new InvalidDisplayDataException("디스플레이 수정 권한이 없습니다.");
        }

        // 상태 업데이트
        display.setDisplayIsPosted(!display.getDisplayIsPosted());
        displayRepository.save(display);  // 변경사항 저장

        return DisplayPostedToggleResponse.builder()
                .displayUid(display.getDisplayUid())
                .displayIsPosted(display.getDisplayIsPosted())
                .build();
    }

    // 유효성 검사를 위한 private 메소드 추가
    private void validateDisplayCreateRequest(DisplayCreateRequest request) {
        if (request == null) {
            throw new InvalidDisplayDataException("요청 데이터가 없습니다.");
        }

        if (request.getDisplayName() == null || request.getDisplayName().trim().isEmpty()) {
            throw new InvalidDisplayDataException("디스플레이 이름은 필수입니다.");
        }

        if (request.getDisplayThumbnailUrl() == null || request.getDisplayThumbnailUrl().trim().isEmpty()) {
            throw new InvalidDisplayDataException("썸네일 URL은 필수입니다.");
        }

        // 텍스트 유효성 검사
        if (request.getTexts() != null) {
            request.getTexts().forEach(text -> {
                if (text.getDisplayTextDetail() == null || text.getDisplayTextDetail().trim().isEmpty()) {
                    throw new InvalidDisplayDataException("텍스트 내용은 필수입니다.");
                }
            });
        }

        // 배경 유효성 검사
        if (request.getBackground() != null) {
            if (request.getBackground().getDisplayColorSolid() == null) {
                throw new InvalidDisplayDataException("배경 색상은 필수입니다.");
            }
        }
    }
}



