package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryDetailResponse;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryResponse;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;

import java.util.List;

public interface CheerService {

    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request);
    public void enterCheerroom(String userId, Long cheerroomId);
    public void leaveCheerroom(String userId, Long cheerroomId);
    public List<CheerHistoryResponse> getUserCheerHistory(String userId);
    public CheerHistoryDetailResponse getCheerHistoryDetail(String userId, Long cheerId);
}
