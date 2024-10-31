package com.d209.welight.domain.display.service;

import com.d209.welight.domain.display.dto.request.DisplayCreateRequest;
import com.d209.welight.domain.display.dto.request.DisplayDetailRequest;
import com.d209.welight.domain.display.dto.response.DisplayCreateResponse;
import com.d209.welight.domain.display.dto.response.DisplayDetailResponse;

public interface DisplayService {

    // 디스플레이 정보 저장 (기본 정보, 배경, 이미지, 텍스트, 태그)
    DisplayCreateResponse createDisplay(DisplayCreateRequest request);

    // 디스플레이 상세 보기
    DisplayDetailResponse getDisplayDetail(DisplayDetailRequest request);
}
