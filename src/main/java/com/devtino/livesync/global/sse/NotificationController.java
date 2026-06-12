package com.devtino.livesync.global.sse;

import com.devtino.livesync.domain.notification.entity.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.global.config.JwtTokenProvider;

import java.util.List;

@Tag(name = "Notifications", description = "알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    // SSE 연결 (workspace 기준으로 구독)
    @Operation(summary = "SSE 구독", description = "서버와 연결을 유지하며 실시간 알림을 수신한다.")
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam String token) {

        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));

        // 추가: 토큰에서 workspaceId 추출
        Long workspaceId = jwtTokenProvider.getWorkspaceId(token);

        return notificationService.connect(workspaceId, memberId);
    }

    // 테스트 알림
    @Operation(summary = "알림 테스트", description = "테스트용 알림을 강제로 보낸다.")
    @GetMapping("/test")
    public void test() {
        notificationService.send(
                1L, // workspaceId
                1L, // memberId
                "테스트 제목",
                "알림 도착!",
                NotificationType.SCHEDULE,
                "/test-url"
        );
    }

    // 알림 목록 조회
    @Operation(summary = "알림 목록 조회", description = "사용자의 전체 알림을 최신순으로 조회한다.")
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

    // unread count
    @GetMapping("/unread-count")
    public long getUnreadCount(@RequestParam String token) {
        Long memberId = Long.parseLong(jwtTokenProvider.getMemberId(token));
        return notificationService.getUnreadCount(memberId);
    }
}