package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.user.entity.User;

import java.util.List;

public interface CheerService {

    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request);
    public List<CheerroomResponse> getAllCheerroomsByGeo(FindByGeoRequest findByGeoRequest);

    public void delegateLeader(long roomId, User currentLeader, User newLeader);
    /* 기록 */
    public void createRecords(User user, long roomId, CheerRecordRequest cheerRecordRequest);
}
