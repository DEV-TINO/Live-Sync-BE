package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.global.config.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "Notifications", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    // SSE 연결
    @Operation(summary = "SSE 구독", description = "실시간 알림 수신")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String token) {

        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        Long workspaceId;
        try {
            workspaceId = jwtTokenProvider.getWorkspaceId(token);
        } catch (Exception e) {
            workspaceId = 0L;
        }

        System.out.println("SUBSCRIBE memberId = " + memberId);

        return notificationService.connect(workspaceId, memberId);
    }

    // 테스트 알림
    @GetMapping("/test")
    public void test() {
        notificationService.send(
                1L,
                1L,
                "테스트 제목",
                "알림 도착!",
                NotificationType.SCHEDULE,
                "/test-url"
        );
    }

    // 알림 목록
    @GetMapping
    public List<Notification> getNotifications(@RequestParam String token) {

        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        return notificationService.getNotifications(memberId);
    }

    // 읽음 처리
    @PatchMapping("/{id}/read")
    public void readNotification(@PathVariable Long id) {
        notificationService.readNotification(id);
    }

    // 안읽은 개수
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam String token) {

        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        return notificationService.getUnreadCount(memberId);
    }
}