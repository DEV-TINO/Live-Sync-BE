package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private final NotificationEmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // UTF-8로 변환
            String body = new String(message.getBody(), StandardCharsets.UTF_8);

            // JSON → Notification 객체 변환
            Notification notification =
                    objectMapper.readValue(body, Notification.class);

            SseEmitter emitter =
                    emitterRepository.get(notification.getMemberId());

            if (emitter != null) {
                emitter.send(
                        SseEmitter.event()
                                .name("notification")
                                .data(notification)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}