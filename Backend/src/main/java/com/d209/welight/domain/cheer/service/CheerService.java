package com.d209.welight.domain.cheer.service;

import com.d209.welight.domain.cheer.dto.request.CheerRecordRequest;
import com.d209.welight.domain.cheer.dto.request.CheerroomCreateRequest;
import com.d209.welight.domain.cheer.dto.request.FindByGeoRequest;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryDetailResponse;
import com.d209.welight.domain.cheer.dto.response.CheerHistoryResponse;
import com.d209.welight.domain.cheer.dto.response.CheerroomResponse;
import com.d209.welight.domain.cheer.dto.response.ParticipantsResponse;
import com.d209.welight.domain.user.entity.User;

import java.util.List;

public interface CheerService {

    public CheerroomResponse createCheerroom(String userId, CheerroomCreateRequest request);
    public List<CheerroomResponse> getAllCheerroomsByGeo(FindByGeoRequest findByGeoRequest);
    public List<ParticipantsResponse> getParticipants(Long cheerId);

    public void delegateLeader(long roomId, User currentLeader, User newLeader);
//    public void endCheering(User user, long cheerNumber);

    /* 기록 */
    public void createRecords(User user, long roomId, CheerRecordRequest cheerRecordRequest);
    public void deleteRecords(User user, long roomId);

    public void enterCheerroom(String cheerNumber, Long cheerroomId);
    public void leaveCheerroom(String cheerNumber, Long cheerroomId);
    public List<CheerHistoryResponse> getUserCheerHistory(String userId);
    public CheerHistoryDetailResponse getCheerHistoryDetail(String userId, Long cheerId);

    public CheerHistoryResponse useDisplayForCheer(Long cheerroomId, String userId, Long displayId);
}
