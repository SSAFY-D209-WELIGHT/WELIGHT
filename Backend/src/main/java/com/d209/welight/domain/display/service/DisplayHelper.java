package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.DisplayBackgroundDto;
import com.d209.welight.domain.display.dto.DisplayImageDto;
import com.d209.welight.domain.display.dto.DisplayTextDto;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.entity.*;
import com.d209.welight.domain.display.entity.displaystorage.DisplayStorage;
import com.d209.welight.domain.display.repository.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.global.service.s3.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class DisplayHelper {

    private final DisplayRepository displayRepository;
    private final DisplayTagRepository displayTagRepository;
    private final DisplayImageRepository displayImageRepository;
    private final DisplayTextRepository displayTextRepository;
    private final DisplayBackgroundRepository displayBackgroundRepository;
    private final DisplayStorageRepository displayStorageRepository;
    private final S3Service s3Service;

    public Display createAndSaveDisplay(User user, DisplayCreateRequest request) {
        Display display = Display.builder()
                .creatorUid(user.getUserUid())
                .displayName(request.getDisplayName())
                .displayThumbnailUrl(request.getDisplayThumbnailUrl())
                .displayIsPosted(request.getDisplayIsPosted())
                .displayCreatedAt(LocalDateTime.now())
                .displayDownloadCount(0L)
                .displayLikeCount(0L)
                .build();

        Display savedDisplay = displayRepository.save(display);
        log.debug("디스플레이 ID: {}로 기본 정보 저장 완료", savedDisplay.getDisplayUid());
        return savedDisplay;
    }

    // 디스플레이 텍스트 저장
    public void saveTags(Display display, List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            List<DisplayTag> displayTags = tags.stream()
                    .map(tag -> createDisplayTag(display, tag))
                    .collect(Collectors.toList());
            displayTagRepository.saveAll(displayTags);
            log.debug("디스플레이 ID: {}에 대해 {} 개의 태그 저장 완료", display.getDisplayUid(), displayTags.size());
        }
    }

    private DisplayTag createDisplayTag(Display display, String tag) {
        DisplayTag displayTag = new DisplayTag();
        displayTag.setDisplay(display);
        displayTag.setDisplayTagText(tag);
        displayTag.setDisplayTagCreatedAt(LocalDateTime.now());
        return displayTag;
    }

    // 디스플레이 이미지 저장
    public void saveImages(Display display, List<DisplayImageDto> imageDtos) {
        if (imageDtos != null && !imageDtos.isEmpty()) {
            List<DisplayImage> images = imageDtos.stream()
                    .map(imageDto -> createDisplayImage(display, imageDto))
                    .collect(Collectors.toList());
            displayImageRepository.saveAll(images);
            display.setImages(images);
            log.debug("디스플레이 ID: {}에 대해 {} 개의 이미지 저장 완료", display.getDisplayUid(), images.size());
        }
    }

    public DisplayImage createDisplayImage(Display display, DisplayImageDto imageDto) {
        DisplayImage image = new DisplayImage();
        image.setDisplay(display);
        image.setDisplayImgUrl(imageDto.getDisplayImgUrl());
        image.setDisplayImgColor(imageDto.getDisplayImgColor());
        image.setDisplayImgScale(imageDto.getDisplayImgScale());
        image.setDisplayImgRotation(imageDto.getDisplayImgRotation());
        image.setDisplayImgOffsetx(imageDto.getDisplayImgOffsetx());
        image.setDisplayImgOffsety(imageDto.getDisplayImgOffsety());
        image.setDisplayImgCreatedAt(LocalDateTime.now());
        return image;
    }

    // 디스플레이 텍스트 저장
    public void saveTexts(Display display, List<DisplayTextDto> textDtos) {
        if (textDtos != null && !textDtos.isEmpty()) {
            List<DisplayText> texts = textDtos.stream()
                    .map(textDto -> createDisplayText(display, textDto))
                    .collect(Collectors.toList());
            displayTextRepository.saveAll(texts);
            display.setTexts(texts);
            log.debug("디스플레이 ID: {}에 대해 {} 개의 텍스트 저장 완료", display.getDisplayUid(), texts.size());
        }
    }

    private DisplayText createDisplayText(Display display, DisplayTextDto textDto) {
        DisplayText text = new DisplayText();
        text.setDisplay(display);
        text.setDisplayTextDetail(textDto.getDisplayTextDetail());
        text.setDisplayTextColor(textDto.getDisplayTextColor());
        text.setDisplayTextFont(textDto.getDisplayTextFont());
        text.setDisplayTextRotation(textDto.getDisplayTextRotation());
        text.setDisplayTextScale(textDto.getDisplayTextScale());
        text.setDisplayTextOffsetx(textDto.getDisplayTextOffsetx());
        text.setDisplayTextOffsety(textDto.getDisplayTextOffsety());
        text.setDisplayTextCreatedAt(LocalDateTime.now());
        return text;
    }

    // 디스플레이 배경 저장
    public void saveBackground(Display display, DisplayBackgroundDto backgroundDto) {
        if (backgroundDto != null) {
            DisplayBackground background = createDisplayBackground(display, backgroundDto);
            displayBackgroundRepository.save(background);
            display.setBackground(background);
            log.debug("디스플레이 ID: {}에 대한 배경 정보 저장 완료", display.getDisplayUid());
        }
    }

    // 디스플레이 저장소 저장
    public void saveDisplayStorage(User user, Display display) {
        DisplayStorage displayStorage = DisplayStorage.builder()
                .user(user)
                .display(display)
                .downloadAt(LocalDateTime.now())
                .isFavorites(false)
                .favoritesAt(null)
                .build();
        displayStorageRepository.save(displayStorage);
    }

    private DisplayBackground createDisplayBackground(Display display, DisplayBackgroundDto backgroundDto) {
        DisplayBackground background = new DisplayBackground();
        background.setDisplay(display);
        background.setDisplayBackgroundBrightness(backgroundDto.getDisplayBackgroundBrightness());
        background.setDisplayColorSolid(backgroundDto.getDisplayColorSolid());
        background.setDisplayBackgroundGradationColor1(backgroundDto.getDisplayBackgroundGradationColor1());
        background.setDisplayBackgroundGradationColor2(backgroundDto.getDisplayBackgroundGradationColor2());
        background.setDisplayBackgroundGradationType(backgroundDto.getDisplayBackgroundGradationType());
        background.setDisplayBackgroundCreatedAt(LocalDateTime.now());
        return background;
    }

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

    @Transactional
    public void duplicateImages(List<DisplayImage> originalImages, Display newDisplay, String userId) {
        try {
            if (originalImages != null) {
                originalImages.forEach(image -> {
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
                });
            }

        } catch (Exception e) {
            throw new RuntimeException("이미지 복제 중 오류가 발생했습니다.");
        }
    }

    public String generateFileName(String userId, String type, String originalFileUrl) {
        // 현재 timestamp를 이용해 고유성 보장
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 원본 파일명 추출 (URL의 마지막 '/' 이후 부분)
        String originalFileName = originalFileUrl.substring(originalFileUrl.lastIndexOf('/') + 1);

        // 확장자를 제외한 파일명 추출
        String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

        // 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));

        // 파일명에서 특수문자와 공백을 제거하고 안전한 문자로 대체
        String safeFileName = fileNameWithoutExtension
        .replaceAll("[^a-zA-Z0-9가-힣]", "_")  // 특수문자와 공백을 언더스코어로 대체
        .replaceAll("_{2,}", "_")              // 연속된 언더스코어를 하나로 통합
        .trim();                               // 앞뒤 공백 제거

        // 경로 구조: {userId}/{type}/{안전한파일명}_{timestamp}{확장자}
        return String.format("%s/%s/%s_%s%s",
                userId,
                type,
                safeFileName,
                timestamp,
                extension);
    }

    // Display 생성 요청의 유효성 검사
    public void validateDisplayCreateRequest(DisplayCreateRequest request) {

        if (request.getDisplayName() == null || request.getDisplayName().trim().isEmpty()) {
            throw new IllegalStateException("디스플레이 이름은 필수입니다.");
        }

        if (request.getDisplayThumbnailUrl() == null || request.getDisplayThumbnailUrl().trim().isEmpty()) {
            throw new IllegalStateException("썸네일 URL은 필수입니다.");
        }

        // 텍스트 유효성 검사
        if (request.getTexts() != null) {
            request.getTexts().forEach(text -> {
                if (text.getDisplayTextDetail() == null || text.getDisplayTextDetail().trim().isEmpty()) {
                    throw new IllegalStateException("텍스트 내용은 필수입니다.");
                }
            });
        }

        // 배경 유효성 검사
        if (request.getBackground() != null) {
            if (request.getBackground().getDisplayColorSolid() == null) {
                throw new IllegalStateException("배경 색상은 필수입니다.");
            }
        }
    }

    // 디스플레이로 부터 연관 테이블의 정보 가져오기
    public List<String> getTagsFromDisplay(Display display, List<String> requestTags) {
        // 태그 정보 변환
//        return display.getTags().stream()
//                .map(DisplayTag::getDisplayTagText)
//                .collect(Collectors.toList());
        return requestTags != null ? requestTags :
                display.getTags().stream()
                        .map(DisplayTag::getDisplayTagText)
                        .toList();
    }

    public List<DisplayImageDto> getImagesFromDisplay(Display display) {
        return display.getImages().stream()
                .map(image -> DisplayImageDto.builder()
                        .displayImgUrl(image.getDisplayImgUrl())
                        .displayImgColor(image.getDisplayImgColor())
                        .displayImgScale(image.getDisplayImgScale())
                        .displayImgRotation(image.getDisplayImgRotation())
                        .displayImgOffsetx(image.getDisplayImgOffsetx())
                        .displayImgOffsety(image.getDisplayImgOffsety())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DisplayTextDto> getTextsFromDisplay(Display display) {
        return display.getTexts().stream()
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
    }

    public DisplayBackgroundDto getBackgroundFromDisplay(Display display) {
        return DisplayBackgroundDto.builder()
                .displayBackgroundBrightness(display.getBackground().getDisplayBackgroundBrightness())
                .displayColorSolid(display.getBackground().getDisplayColorSolid())
                .displayBackgroundGradationColor1(display.getBackground().getDisplayBackgroundGradationColor1())
                .displayBackgroundGradationColor2(display.getBackground().getDisplayBackgroundGradationColor2())
                .displayBackgroundGradationType(display.getBackground().getDisplayBackgroundGradationType())
                .build();
    }

    public List<DisplayImageDto> convertImagesToDto(List<DisplayImage> images) {
        return images.stream()
                .map(image -> DisplayImageDto.builder()
                        .displayImgUrl(image.getDisplayImgUrl())
                        .displayImgColor(image.getDisplayImgColor())
                        .displayImgScale(image.getDisplayImgScale())
                        .displayImgRotation(image.getDisplayImgRotation())
                        .displayImgOffsetx(image.getDisplayImgOffsetx())
                        .displayImgOffsety(image.getDisplayImgOffsety())
                        .build())
                .collect(Collectors.toList());
    }

    public List<DisplayTextDto> convertTextsToDto(List<DisplayText> texts) {
        return texts.stream()
                .map(text -> DisplayTextDto.builder()
                        .displayTextDetail(text.getDisplayTextDetail())
                        .displayTextColor(text.getDisplayTextColor())
                        .displayTextFont(text.getDisplayTextFont())
                        .displayTextScale(text.getDisplayTextScale())
                        .displayTextRotation(text.getDisplayTextRotation())
                        .displayTextOffsetx(text.getDisplayTextOffsetx())
                        .displayTextOffsety(text.getDisplayTextOffsety())
                        .build())
                .collect(Collectors.toList());
    }

    public DisplayBackgroundDto convertBackgroundToDto(DisplayBackground background) {
        return DisplayBackgroundDto.builder()
                .displayBackgroundBrightness(background.getDisplayBackgroundBrightness())
                .displayColorSolid(background.getDisplayColorSolid())
                .displayBackgroundGradationColor1(background.getDisplayBackgroundGradationColor1())
                .displayBackgroundGradationColor2(background.getDisplayBackgroundGradationColor2())
                .displayBackgroundGradationType(background.getDisplayBackgroundGradationType())
                .build();
    }

}
