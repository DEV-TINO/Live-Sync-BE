package com.devtino.livesync.global.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationEmitterRepository {

    // 유저별 emitter 저장
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // emitter 저장
    public void save(String key, SseEmitter emitter) {
        emitters.put(key, emitter);
    }

    // emitter 조회
    public SseEmitter get(String key) {
        return emitters.get(key);
    }

    // emitter 삭제
    public void delete(String key) {
        emitters.remove(key);
    }
}