package com.devtino.livesync.global.sse;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    // Object → String 으로 변경
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CHANNEL = "notification";

    public void publish(Object message) {
        try {
            // 객체 → JSON 문자열로 변환
            String json = objectMapper.writeValueAsString(message);

            // JSON 그대로 Redis에 발행
            redisTemplate.convertAndSend(CHANNEL, json);
        } catch (Exception e) {
            throw new RuntimeException("Redis publish 실패");
        }
    }
}