package com.duyhung.lydinc_backend.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveRPTokenToCache(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(5));
    }

    public String getCacheValue(String key) {
        return redisTemplate.opsForValue().get(key).toString();
    }

    public void deleteCache(String key) {
        redisTemplate.delete(key);
    }

}
