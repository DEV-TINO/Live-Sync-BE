package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository; // 알림 DB 저장용

    // 연결 생성
    public SseEmitter connect(Long memberId) {

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 연결 1시간 유지 -> 시간 지나면 자동으로 끊음

        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(memberId)); // 정상 종료
        emitter.onTimeout(() -> emitterRepository.delete(memberId)); // 시간 초과

        return emitter; // 클라이언트에게 반환 -> 브라우저가 연결 유지 시작
    }

    // 알림 생성 + 저장 + 실시간 전송
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

        SseEmitter emitter = emitterRepository.get(memberId); // 해당 유저 현재 연결되어있는지 확인

        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification)); // 전체 객체 전달
        } catch (IOException e) {
            emitterRepository.delete(memberId);
        }
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

        // 수정된 부분: DB에 반영되도록 save 추가
        notificationRepository.save(notification);
    }

    // unread count 조회
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId);
    }
}