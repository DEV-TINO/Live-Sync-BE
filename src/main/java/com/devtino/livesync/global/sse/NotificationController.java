package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Notifications", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 연결
    @Operation(summary = "SSE 구독", description = "실시간 알림 수신")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false, defaultValue = "0") Long workspaceId
    ) {
        return notificationService.connect(workspaceId, memberId);
    }

    // 테스트 알림
    @GetMapping("/test")
    public void test(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(defaultValue = "1") Long workspaceId
    ) {
        notificationService.send(
                workspaceId,
                memberId,
                "테스트 제목",
                "알림 도착!",
                NotificationType.SCHEDULE,
                "/test-url"
        );
    }

    // 알림 목록
    @GetMapping
    public List<Notification> getNotifications(@AuthenticationPrincipal Long memberId) {
        return notificationService.getNotifications(memberId);
    }

    // 읽음 처리
    @PatchMapping("/{id}/read")
    public void readNotification(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long id
    ) {
        notificationService.readNotification(memberId, id);
    }

    // 안읽은 개수
    @GetMapping("/unread-count")
    public long getUnreadCount(@AuthenticationPrincipal Long memberId) {
        return notificationService.getUnreadCount(memberId);
    }
}
