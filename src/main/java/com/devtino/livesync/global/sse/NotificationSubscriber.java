package com.devtino.livesync.global.sse;

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
            String body = new String(message.getBody(), StandardCharsets.UTF_8);

            // DTO로 받기
            NotificationMessage msg =
                    objectMapper.readValue(body, NotificationMessage.class);

            // NotificationService.connect와 같은 member 기준 key를 사용한다.
            String key = msg.getMemberId().toString();

            SseEmitter emitter = emitterRepository.get(key);

            if (emitter != null) {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .name("notification")
                                    .data(msg)
                    );
                } catch (Exception e) {
                    emitterRepository.delete(key);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
