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

    // 수정하는 디스플레이 정보 전송
    public DisplayCreateRequest getDisplayForEdit(Long displayId, String userId);

    // 수정된 디스플레이 저장
    DisplayCreateResponse updateDisplay(Long displayUid, DisplayCreateRequest request, String userId);

    // 디스플레이 삭제
    void deleteDisplay(Long displayUid, String userId);

    // 디스플레이 저장소 - 다운로드, 삭제
    DisplayCreateResponse downloadDisplay(String userId, long displayUid);
    DisplayCreateResponse deleteStoredDisplay(String userId, long displayUid);

    // 디스플레이 즐겨찾기 (저장소에서 & 내가 제작)
    DisplayCreateResponse updateDisplayFavorite(String userId, long displayUid);

    // 디스플레이 좋아요
    void doLikeDisplay(String userId, long displayUid);
    void cancelLikeDisplay(String userId, long displayUid);
    DisplayListResponse getLikedDisplayList(String userId, Pageable pageable);


    // 디스플레이 댓글
    List<DisplayCommentResponse> getComments(String userId, long displayUid);
    void createComment(String userId, Long displayId, DisplayCommentRequest request);
    void updateComment(String userId, Long displayId, DisplayCommentUpdateRequest request);
    void deleteComment(String userId, Long displayId, Long commentUid);

    DisplayPostedToggleResponse updateDisplayStatus(Long displayUid, String userId);

}
