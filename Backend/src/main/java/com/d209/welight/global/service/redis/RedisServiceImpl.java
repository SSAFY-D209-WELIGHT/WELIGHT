package com.d209.welight.global.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh-token.expiretime}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                "RefreshToken:" + userId,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get("RefreshToken:" + userId);
    }

    @Override
    public String findUserIdByRefreshToken(String refreshToken) {
        Set<String> keys = redisTemplate.keys("RefreshToken:*");
        for (String key : Objects.requireNonNull(keys)) {
            String storedToken = redisTemplate.opsForValue().get(key);
            if (refreshToken.equals(storedToken)) {
                return key.substring("RefreshToken:".length());
            }
        }
        return null;
    }

    @Override
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("RefreshToken:" + userId);
    }
}

