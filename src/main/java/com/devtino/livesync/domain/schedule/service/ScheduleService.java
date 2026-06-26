package com.devtino.livesync.domain.schedule.service;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.file.repository.FileRepository;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.schedule.domain.Schedule;
import com.devtino.livesync.domain.schedule.dto.ScheduleCreateRequest;
import com.devtino.livesync.domain.schedule.dto.ScheduleResponseDto;
import com.devtino.livesync.domain.schedule.repository.ScheduleRepository;
import com.devtino.livesync.domain.notification.entity.NotificationType;
import com.devtino.livesync.domain.workspace.entity.MemberWorkspace;
import com.devtino.livesync.domain.workspace.entity.Workspace;
import com.devtino.livesync.domain.workspace.repository.MemberWorkspaceRepository;
import com.devtino.livesync.domain.workspace.repository.WorkspaceRepository;
import com.devtino.livesync.global.sse.NotificationService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final WorkspaceRepository workspaceRepository;
    private final MemberWorkspaceRepository memberWorkspaceRepository;
    private final NotificationService notificationService;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    // 워크스페이스 소속 조회
    private MemberWorkspace getMemberWorkspace(Long memberId, Long workspaceId) {
        return memberWorkspaceRepository
                .findByMemberIdAndWorkspaceId(memberId, workspaceId)
                .orElseThrow(() -> new RuntimeException("워크스페이스 접근 불가"));
    }

    // 관리자 권한 체크
    private void validateAdmin(Long memberId, Long workspaceId) {
        MemberWorkspace mw = getMemberWorkspace(memberId, workspaceId);

        if (mw.getRole() != MemberWorkspace.Role.ADMIN) {
            throw new RuntimeException("관리자 권한 필요");
        }
    }

    /*
     * 일정 생성
     */
    public void createSchedule(Long memberId, ScheduleCreateRequest request, List<MultipartFile> files) {

        validateAdmin(memberId, request.getWorkspaceId());

        Workspace workspace = workspaceRepository.findById(request.getWorkspaceId())
                .orElseThrow(() -> new RuntimeException("워크스페이스 없음"));

        List<Member> showhosts = request.getShowhostLoginIds()
                .stream()
                .map(loginId -> memberRepository.findByLoginId(loginId)
                        .orElseThrow(() -> new RuntimeException("유저 없음: " + loginId)))
                .toList();

        if (showhosts.isEmpty()) {
            throw new RuntimeException("쇼호스트 없음");
        }

        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .color(request.getColor())
                .location(request.getLocation())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .workspace(workspace)
                .showhosts(showhosts)
                .build();

        scheduleRepository.save(schedule);

        for (Member showhost : showhosts) {
            notificationService.send(
                    workspace.getId(),
                    showhost.getId(),
                    "새로운 일정이 배정되었습니다!",
                    schedule.getTitle(),
                    NotificationType.SCHEDULE,
                    "/schedules/" + schedule.getId()
            );
        }

        notificationService.send(
                workspace.getId(),
                memberId,
                "일정 생성 완료",
                schedule.getTitle(),
                NotificationType.SCHEDULE,
                "/schedules/" + schedule.getId()
        );

        if (files != null && !files.isEmpty()) {

            File dir = new File(uploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("파일 저장 실패");
            }

            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename();

                if (file.isEmpty() || originalName == null || originalName.isBlank()) {
                    throw new RuntimeException("업로드할 파일이 없습니다.");
                }

                try {
                    String savedName = UUID.randomUUID() + "_" + originalName;

                    File dest = new File(dir, savedName);
                    file.transferTo(dest);

                    FileEntity entity = FileEntity.builder()
                            .fileName(originalName)
                            .fileKey(savedName)
                            .schedule(schedule)
                            .isPublic(true)
                            .build();

                    FileEntity savedFile = fileRepository.save(entity);
                    savedFile.setFileUrl("/files/download/" + savedFile.getId());
                    fileRepository.save(savedFile);

                } catch (Exception e) {
                    throw new RuntimeException("파일 저장 실패", e);
                }
            }
        }
    }

    /*
     * 전체 일정 (workspace 기준)
     */
    public List<ScheduleResponseDto> getAllSchedules(Long memberId, Long workspaceId) {
        validateAdmin(memberId, workspaceId);

        return scheduleRepository.findByWorkspace_Id(workspaceId)
                .stream()
                .map(ScheduleResponseDto::from)
                .toList();
    }

    /*
     * 내 일정 (workspace + member 기준)
     */
    public List<ScheduleResponseDto> getMySchedules(Long memberId, Long workspaceId) {
        getMemberWorkspace(memberId, workspaceId);

        return scheduleRepository.findByWorkspace_Id(workspaceId)
                .stream()
                .filter(s -> s.getShowhosts()
                        .stream()
                        .anyMatch(m -> m.getId().equals(memberId)))
                .map(ScheduleResponseDto::from)
                .toList();
    }

    public ScheduleResponseDto getSchedule(Long memberId, Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정 없음"));

        boolean hasAccess = memberWorkspaceRepository
                .findByMemberId(memberId)
                .stream()
                .anyMatch(mw ->
                        mw.getWorkspace().getId().equals(schedule.getWorkspace().getId())
                );

        if (!hasAccess) {
            throw new RuntimeException("워크스페이스 접근 불가");
        }

        return ScheduleResponseDto.from(schedule);
    }

    public void updateSchedule(Long memberId, Long scheduleId, ScheduleCreateRequest request) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정 없음"));

        validateAdmin(memberId, schedule.getWorkspace().getId());

        schedule.update(
                request.getTitle(),
                request.getColor(),
                request.getLocation(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getDescription(),
                request.getStartTime(),
                request.getEndTime()
        );

        scheduleRepository.save(schedule);
    }

    public void deleteSchedule(Long memberId, Long scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정 없음"));

        validateAdmin(memberId, schedule.getWorkspace().getId());

        scheduleRepository.delete(schedule);
    }
}
