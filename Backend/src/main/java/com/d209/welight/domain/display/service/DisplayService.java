package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.request.DisplayCommentRequest;
import com.d209.welight.domain.display.dto.request.DisplayCommentUpdateRequest;
import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.DisplayCommentResponse;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;
import com.d209.welight.domain.user.entity.User;

import java.util.List;

public interface DisplayService {

    // 디스플레이 정보 저장 (기본 정보, 배경, 이미지, 텍스트, 태그)
    DisplayCreateResponse createDisplay(DisplayCreateRequest request);

    // 디스플레이 상세 보기
    DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request);

    // 디스플레이 저장소 - 다운로드, 삭제
    void downloadDisplay(User user, long displayUid);
    void deleteStoredDisplay(User user, long displayUid);

    // 디스플레이 즐겨찾기 (저장소에서)
    void updateDisplayFavorite(User user, long displayUid);

    // 디스플레이 좋아요
    void doLikeDisplay(User user, long displayUid);
    void cancelLikeDisplay(User user, long displayUid);

    // 디스플레이 댓글
    List<DisplayCommentResponse> getComments(User user, long displayUid);
    void createComment(User user, Long displayId, DisplayCommentRequest request);
    DisplayCommentResponse updateComment(User user, DisplayCommentUpdateRequest request);
    void deleteComment(User user, Long commentUid);
}
