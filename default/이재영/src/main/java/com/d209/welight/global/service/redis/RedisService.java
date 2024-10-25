package com.d209.welight.global.service.redis;

public interface RedisService {
    void saveRefreshToken(String userProviderId, String refreshToken);
    String getRefreshToken(String userProviderId);
    void deleteRefreshToken(String userProviderId);
}
