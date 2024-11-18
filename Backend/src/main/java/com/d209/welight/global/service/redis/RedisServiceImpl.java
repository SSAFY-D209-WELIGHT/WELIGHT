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

            // JwtToken 객체 문자열에서 refreshToken 부분만 추출해서 비교
            if (storedToken != null && storedToken.contains(refreshToken)) {
                return key.substring("RefreshToken:".length());
            }
        }
        return null;
    }

    @Override
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete("RefreshToken:" + userId);
    }

    private String extractRefreshToken(String jwtTokenString) {
        // "refreshToken=" 다음부터 ")" 전까지의 문자열 추출
        int startIndex = jwtTokenString.indexOf("refreshToken=") + "refreshToken=".length();
        int endIndex = jwtTokenString.lastIndexOf(")");
        return jwtTokenString.substring(startIndex, endIndex);
    }
}

