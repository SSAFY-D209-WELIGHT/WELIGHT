package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.entity.*;
import org.springframework.data.domain.Pageable;
import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.DisplayCommentResponse;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.user.entity.User;
import com.d209.welight.domain.display.dto.response.DisplayListResponse;
import com.d209.welight.domain.display.dto.response.DisplayPostedToggleResponse;

import java.util.List;

public interface DisplayService {

    // 디스플레이 정보 저장 (기본 정보, 배경, 이미지, 텍스트, 태그)
    DisplayCreateResponse createDisplay(String userId, DisplayCreateRequest request);

    // 디스플레이 상세 보기
    DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request);

    // 디스플레이 목록 조회
    DisplayListResponse getDisplayList(Pageable pageable);

    // 나의 디스플레이 목록 조회
    DisplayListResponse getMyDisplayList(String userId, Pageable pageable);

    // 디스플레이 복제
    DisplayCreateResponse duplicateDisplay(Long displayId, String userId);
    void duplicateTexts(List<DisplayText> originalTexts, Display newDisplay);
    void duplicateImages(List<DisplayImage> originalImages, Display newDisplay, String userId);
    void duplicateBackground(DisplayBackground originalBackground, Display newDisplay);

    // 수정하는 디스플레이 정보 전송
    public DisplayCreateRequest getDisplayForEdit(Long displayId, String userId);

    // 수정된 디스플레이 저장
    DisplayCreateResponse updateDisplay(Long displayUid, DisplayCreateRequest request, String userId);

    // 디스플레이 삭제
    void deleteDisplay(Long displayUid, String userId);

    // 디스플레이 저장소 - 다운로드, 삭제
    DisplayCreateResponse downloadDisplay(User user, long displayUid);
    DisplayCreateResponse deleteStoredDisplay(User user, long displayUid);

    // 디스플레이 즐겨찾기 (저장소에서 & 내가 제작)
    void updateDisplayFavorite(User user, long displayUid);

    // 디스플레이 좋아요
    void doLikeDisplay(User user, long displayUid);
    void cancelLikeDisplay(User user, long displayUid);

    // 디스플레이 댓글
    List<DisplayCommentResponse> getComments(User user, long displayUid);
    void createComment(User user, Long displayId, DisplayCommentRequest request);
    void updateComment(User user, Long displayId, DisplayCommentUpdateRequest request);
    void deleteComment(User user, Long displayId, Long commentUid);

    DisplayPostedToggleResponse updateDisplayStatus(Long displayUid, String userId);

}
