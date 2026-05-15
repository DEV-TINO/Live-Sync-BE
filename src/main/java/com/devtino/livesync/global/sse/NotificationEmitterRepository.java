package com.devtino.livesync.global.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationEmitterRepository {

    // 유저별 emitter 저장
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    // emitter 저장
    public void save(Long memberId, SseEmitter emitter) {
        emitters.put(memberId, emitter);
    }

    // emitter 조회
    public SseEmitter get(Long memberId) {
        return emitters.get(memberId);
    }

    // emitter 삭제 (연결 끊겼을 때)
    public void delete(Long memberId) {
        emitters.remove(memberId);
    }
}