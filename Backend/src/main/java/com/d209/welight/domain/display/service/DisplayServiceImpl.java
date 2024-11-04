package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.DisplayCommentResponse;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.display.entity.*;
import com.d209.welight.domain.display.entity.displaylike.DisplayLike;
import com.d209.welight.domain.display.entity.displaystorage.DisplayStorage;
import com.d209.welight.domain.display.repository.*;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.user.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;

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
    private final DisplayStorageRepository displayStorageRepository;
    private final DisplayLikeRepository displayLikeRepository;
    private final DisplayCommentRepository displayCommentRepository;
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
                .displayIsPosted(false)  // 초기 생성시 게시되지 않은 상태
                .displayCreatedAt(LocalDateTime.now())
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
        displayStorageRepository.deleteByUserAndDisplay(user, display);

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
        displayLikeRepository.deleteByUserAndDisplay(user, display);

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
        displayCommentRepository.deleteByCommentUid(commentUid);


    }

}
