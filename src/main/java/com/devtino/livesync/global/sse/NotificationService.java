package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.notification.entity.Notification;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.domain.notification.repository.NotificationRepository;
import com.devtino.livesync.domain.workspace.entity.Workspace;
import com.devtino.livesync.domain.workspace.repository.WorkspaceRepository;
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
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final NotificationPublisher notificationPublisher;

    // SSE 연결
    public SseEmitter connect(Long workspaceId, Long memberId) {

        // 핵심: member 기준으로 통일
        String key = memberId.toString();

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

        emitterRepository.save(key, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(key));
        emitter.onTimeout(() -> emitterRepository.delete(key));

        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected"));
        } catch (IOException e) {
            emitterRepository.delete(key);
        }

        return emitter;
    }

    // 알림 생성 + DB 저장 + Redis 발행
    @Async
    public void send(Long workspaceId, Long memberId,
                     String title, String content,
                     NotificationType type, String url) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("워크스페이스 없음"));

        // DB 저장
        Notification notification = Notification.builder()
                .member(member)
                .workspace(workspace)
                .title(title)
                .content(content)
                .type(type)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // Redis + SSE payload
        NotificationMessage message = new NotificationMessage(
                workspaceId,
                memberId,
                title,
                content,
                type,
                url
        );

        notificationPublisher.publish(message);
    }

    // 목록 조회
    public List<Notification> getNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 읽음 처리
    public void readNotification(Long memberId, Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow();

        if (!notification.getMember().getId().equals(memberId)) {
            throw new RuntimeException("알림 접근 권한 없음");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // 안읽은 개수
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByMemberIdAndIsReadFalse(memberId);
    }
}
