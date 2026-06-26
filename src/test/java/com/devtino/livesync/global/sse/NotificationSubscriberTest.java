package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.Message;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationSubscriberTest {

    private final NotificationEmitterRepository emitterRepository = new NotificationEmitterRepository();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationSubscriber subscriber = new NotificationSubscriber(emitterRepository, objectMapper);

    @Test
    void onMessageFindsEmitterByMemberId() throws Exception {
        SseEmitter emitter = mock(SseEmitter.class);
        emitterRepository.save("7", emitter);

        NotificationMessage notification = new NotificationMessage(
                3L,
                7L,
                "title",
                "content",
                NotificationType.SCHEDULE,
                "/schedules/1"
        );

        subscriber.onMessage(redisMessage(objectMapper.writeValueAsString(notification)), null);

        verify(emitter).send(any(SseEmitter.SseEventBuilder.class));
    }

    private Message redisMessage(String body) {
        return new Message() {
            @Override
            public byte[] getBody() {
                return body.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public byte[] getChannel() {
                return "notification".getBytes(StandardCharsets.UTF_8);
            }
        };
    }
}
