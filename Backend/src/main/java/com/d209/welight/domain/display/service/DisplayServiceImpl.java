package com.d209.welight.domain.display.service;

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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.d209.welight.domain.display.dto.response.DisplayListResponse;

@Service
@RequiredArgsConstructor
@Transactional
public class DisplayServiceImpl implements DisplayService {

    private final DisplayRepository displayRepository;
    private final DisplayTagRepository displayTagRepository;
    private final DisplayImageRepository displayImageRepository;
    private final DisplayTextRepository displayTextRepository;
    private final DisplayBackgroundRepository displayBackgroundRepository;
    private final DisplayColorRepository displayColorRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 디스플레이를 생성합니다.
     * @param request 생성할 디스플레이 정보
     * @return DispalayCreateReponse
     */
    @Override
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
            //Display 존재 여부 확인
            Display display = displayRepository.findById(request.getDisplayUid())
                    .orElseThrow(() -> new EntityNotFoundException("디스플레이를 찾을 수 없습니다."));

            // 태그 정보 조회
            List<DisplayTag> tags = displayTagRepository.findByDisplay(display);

            // 제작자 정보 조회 (User 테이블에서)
            String creatorId = userRepository.findById(display.getCreatorUid())
                    .map(User::getUserId) // User가 존재하면 UserId 반환
                    .orElse("존재하지 않는 아이디"); // User가 없으면 기본 메시지 반환

            // 현재 인증된 사용자의 ID와 디스플레이 소유자 ID 비교
            boolean isOwner = display.getCreatorUid().equals(request.getUserId());

            // 4. Response 객체 생성 및 반환
            return DisplayDetailResponse.builder()
                    .creatorId(Long.valueOf(creatorId))
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
                    .displayId(display.getDisplayUid())
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

}
