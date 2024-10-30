package com.d209.welight.global.service.redis;

public interface RedisService {
    void saveRefreshToken(String userId, String refreshToken);
    String getRefreshToken(String userId);
    String findUserIdByRefreshToken(String refreshToken);
    void deleteRefreshToken(String userId);
}
