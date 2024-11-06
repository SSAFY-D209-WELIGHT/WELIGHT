package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;

public interface CheerService {

    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request);
    public void enterCheerroom(String userId, Long cheerroomId);
}
