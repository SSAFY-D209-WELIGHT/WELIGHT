package com.d209.welight.domain.display.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayBackground;
import com.d209.welight.domain.display.entity.DisplayColor;
import com.d209.welight.domain.display.entity.DisplayImage;
import com.d209.welight.domain.display.entity.DisplayTag;
import com.d209.welight.domain.display.entity.DisplayText;
import com.d209.welight.domain.display.repository.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import com.d209.welight.global.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.d209.welight.domain.display.dto.response.DisplayListResponse;

@Service
@RequiredArgsConstructor
public class DisplayServiceImpl implements DisplayService {

    private final DisplayRepository displayRepository;
    private final DisplayTagRepository displayTagRepository;
    private final DisplayImageRepository displayImageRepository;
    private final DisplayTextRepository displayTextRepository;
    private final DisplayBackgroundRepository displayBackgroundRepository;
    private final DisplayColorRepository displayColorRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    /**
     * 새로운 디스플레이를 생성합니다.
     * @param request 생성할 디스플레이 정보
     * @return DispalayCreateReponse
     */
    @Override
    @Transactional
    public DisplayCreateResponse createDisplay(DisplayCreateRequest request) {
        try {
            // 1. Display 엔티티 생성
            Display display = Display.builder()
                .creatorUid(request.getCreatorUid())
                .displayName(request.getDisplayName())
                .displayThumbnailUrl(request.getDisplayThumbnailUrl())
                .displayIsPosted(request.getDisplayIsPosted())  // 초기 생성시 게시되지 않은 상태
                .displayCreatedAt(LocalDateTime.now())
                .displayUpdatedAt(LocalDateTime.now())
                .displayDownloadCount(0L)
                .displayLikeCount(0L)
                .build();

            // 1. 디스플레이 기본 정보 저장
            Display savedDisplay = displayRepository.save(display);

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
                        image.setDisplayImgPosition(imageDto.getDisplayImgPosition());
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
                        text.setDisplayTextPosition(textDto.getDisplayTextPosition());
                        text.setDisplayTextRotation(textDto.getDisplayTextRotation());
                        text.setDisplayTextFont(textDto.getDisplayTextFont());
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
                DisplayColor color = new DisplayColor();
                background.setDisplay(savedDisplay);
                background.setDisplayBackgroundBrightness(request.getBackground().getDisplayBackgroundBrightness());
                background.setDisplayBackgroundCreatedAt(LocalDateTime.now());

                // 배경 색상 정보 저장
                if (request.getBackground().getColor() != null 
                    && request.getBackground().getColor().getDisplayColorSolid() != null) {

                    color.setDisplayBackground(background);
                    color.setDisplayColorSolid(request.getBackground().getColor().getDisplayColorSolid());
                    color.setDisplayBackgroundGradationColor1(request.getBackground().getColor().getDisplayBackgroundGradationColor1());
                    color.setDisplayBackgroundGradationColor2(request.getBackground().getColor().getDisplayBackgroundGradationColor2());
                    color.setDisplayBackgroundGradationType(request.getBackground().getColor().getDisplayBackgroundGradationType());
                }

                displayBackgroundRepository.save(background);
                displayColorRepository.save(color);
                savedDisplay.setBackground(background);

            }

            return DisplayCreateResponse.builder()
                    .displayUid(savedDisplay.getDisplayUid())
                    .displayName(savedDisplay.getDisplayName())
                    .message("디스플레이가 성공적으로 생성되었습니다.")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("디스플레이 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request) {
        try {
            // Display 존재 여부 확인
            Display display = displayRepository.findById(request.getDisplayUid())
                    .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

            // 태그 정보 조회
            List<DisplayTag> tags = displayTagRepository.findByDisplay(display);

            // 현재 사용자의 userId를 userUid로 변환하여 비교
            boolean isOwner = false;
            if (request.getUserId() != null) {
                Optional<User> currentUser = userRepository.findByUserId(request.getUserId());
                if (currentUser.isPresent()) {
                    isOwner = display.getCreatorUid().equals(currentUser.get().getUserUid());
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
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("디스플레이 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
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
            throw new RuntimeException("디스플레이 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public DisplayListResponse getMyDisplayList(String userId, Pageable pageable) {
        try {
            // 사용자 id를 통해 userUid 조회
            Optional<User> user = userRepository.findByUserId(userId);
            Long userUid = user.get().getUserUid();
            Page<Display> displays = displayRepository.findAllByCreatorUid(userUid, pageable);
            
            // 사용자의 디스플레이 목록 조회
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
            throw new RuntimeException("내 디스플레이 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long duplicateDisplay(Long displayId, String userId) {
        // 원본 디스플레이 조회
        Display originalDisplay = displayRepository.findById(displayId)
                .orElseThrow(() -> new NotFoundException("디스플레이를 찾을 수 없습니다."));

        // userUid로 userId 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
        Long userUid = user.getUserUid();

        // 새로운 디스플레이 생성
        Display newDisplay = Display.builder()
                .creatorUid(userUid)
                .displayName(originalDisplay.getDisplayName() + "_복제")
                .displayIsPosted(false)
                .build();

        // 썸네일 복제
        try {
            String originalThumbnailUrl = originalDisplay.getDisplayThumbnailUrl();
            if (originalThumbnailUrl != null && !originalThumbnailUrl.isEmpty()) {
                // 새로운 썸네일 파일명 생성
                String newThumbnailName = generateFileName(userId, "thumbnails", originalThumbnailUrl);

                // S3에 썸네일 복사
                String newThumbnailUrl = s3Service.copyS3(
                        originalThumbnailUrl,
                        newThumbnailName,
                        userId
                );

                // 새로운 디스플레이에 썸네일 URL 설정
                newDisplay.setDisplayThumbnailUrl(newThumbnailUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("썸네일 복제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        // 디스플레이 정보 저장
        Display savedDisplay = displayRepository.save(newDisplay);
        Long newDisplayId = savedDisplay.getDisplayUid();

        // 배경 복제
        duplicateBackground(originalDisplay.getBackground(), savedDisplay);

        // 텍스트 복제
        duplicateTexts(originalDisplay.getTexts(), savedDisplay);

        // 이미지 복제
        duplicateImages(originalDisplay.getImages(), savedDisplay, userId);

        return newDisplayId;
    }

    @Override
    @Transactional
    public void duplicateTexts(List<DisplayText> originalTexts, Display newDisplay) {
        //  디스플레이 정보 수정 후 나머지 텍스트 정보 복제
        if (originalTexts != null) {
            originalTexts.forEach(text -> {
                DisplayText newText = DisplayText.builder()
                        .display(newDisplay)
                        .displayTextDetail(text.getDisplayTextDetail())
                        .displayTextFont(text.getDisplayTextFont())
                        .displayTextColor(text.getDisplayTextColor())
                        .displayTextPosition(text.getDisplayTextPosition())
                        .displayTextRotation(text.getDisplayTextRotation())
                        .build();

                // 디스플레이 텍스트 저장
                displayTextRepository.save(newText);
            });
        }
    }

    @Override
    @Transactional
    public void duplicateImages(List<DisplayImage> originalImages, Display newDisplay, String userId) {
        if (originalImages != null) {
            for (DisplayImage image : originalImages) {
                try {
                    String originalImgUrl = image.getDisplayImgUrl();
                    if (originalImgUrl != null && !originalImgUrl.isEmpty()) {
                        // 새로운 파일명 생성
                        String newFileName = generateFileName(userId, "images", originalImgUrl);
                        
                        // S3에 이미지 복사
                        String newImgUrl = s3Service.copyS3(originalImgUrl, newFileName, userId);
                        
                        // 새로운 이미지 엔티티 생성 및 저장
                        DisplayImage newImage = DisplayImage.builder()
                            .display(newDisplay)
                            .displayImgUrl(newImgUrl)
                            .displayImgPosition(image.getDisplayImgPosition())
                            .displayImgCreatedAt(LocalDateTime.now())
                            .build();
                        
                        displayImageRepository.save(newImage);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("이미지 복제 중 오류가 발생했습니다: " + e.getMessage());
                }
            }
        }
    }

    @Override
    @Transactional
    public void duplicateBackground(DisplayBackground originalBackground, Display newDisplay) {
        if (originalBackground != null) {

            // 새로운 배경 엔티티 생성
            DisplayBackground newBackground = DisplayBackground.builder()
                    .display(newDisplay)
                    .displayBackgroundBrightness(originalBackground.getDisplayBackgroundBrightness())
                    .build();

            // 디스플레이 배경 저장
            DisplayBackground savedBackground = displayBackgroundRepository.save(newBackground);

            // 원본 배경의 색상 정보 조회
            DisplayColor originalColor =  displayColorRepository.findByDisplayBackground(originalBackground)
                    .orElse(null);

            // 배경 색상 복제
            if (originalColor != null) {
                DisplayColor newColor = DisplayColor.builder()
                        .displayBackground(savedBackground)
                        .displayColorSolid(originalColor.getDisplayColorSolid())
                        .displayBackgroundGradationColor1(originalColor.getDisplayBackgroundGradationColor1())
                        .displayBackgroundGradationColor2(originalColor.getDisplayBackgroundGradationColor2())
                        .displayBackgroundGradationType(originalColor.getDisplayBackgroundGradationType())
                        .build();


                // 배경 색상 저장
                displayColorRepository.save(newColor);
            }
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
}



