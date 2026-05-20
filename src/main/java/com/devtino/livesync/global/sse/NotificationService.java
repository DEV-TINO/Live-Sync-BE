package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    /*
     * Redis Publisher 추가
     */
    private final NotificationPublisher notificationPublisher;

    // 연결 생성
    public SseEmitter connect(Long memberId) {

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(memberId));
        emitter.onTimeout(() -> emitterRepository.delete(memberId));

        return emitter;
    }

    /*
     * 알림 생성 + 저장 + Redis publish
     */
    @Async
    public void send(Long memberId, String title, String content,
                     NotificationType type, String url) {

        // 알림을 먼저 DB에 저장 (오프라인 대비)
        Notification notification = Notification.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .type(type)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        /*
         * 기존 SSE 직접 전송 제거
         * Redis로 메시지 발행
         */
        notificationPublisher.publish(notification);
    }

    // 알림 목록 조회
    public List<Notification> getNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 읽음 처리
    public void readNotification(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow();

        notification.setRead(true);

        // DB 반영
        notificationRepository.save(notification);
    }

    // unread count 조회
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId);
    }
}