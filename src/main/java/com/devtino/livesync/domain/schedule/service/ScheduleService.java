package com.devtino.livesync.domain.schedule.service;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.file.repository.FileRepository;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.schedule.domain.Schedule;
import com.devtino.livesync.domain.schedule.dto.ScheduleCreateRequest;
import com.devtino.livesync.domain.schedule.dto.ScheduleResponseDto;
import com.devtino.livesync.domain.schedule.dto.ShowhostDto;
import com.devtino.livesync.domain.schedule.dto.FileDto;
import com.devtino.livesync.domain.schedule.repository.ScheduleRepository;
import com.devtino.livesync.domain.notification.entity.NotificationType;
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

    // SSE 알림 서비스
    private final NotificationService notificationService;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    /*
     * 일정 생성 + 쇼호스트 배정 + 파일 업로드
     */
    public void createSchedule(ScheduleCreateRequest request, List<MultipartFile> files) {

        // 1. 쇼호스트 조회
        List<Member> showhosts = memberRepository.findAllById(request.getShowhostIds());

        if (showhosts.isEmpty()) {
            throw new RuntimeException("선택된 쇼호스트가 존재하지 않습니다.");
        }

        // 2. 일정 생성
        Schedule schedule = Schedule.builder()
                .title(request.getTitle())
                .color(request.getColor())
                .location(request.getLocation())
                .address(request.getAddress())      // 주소
                .latitude(request.getLatitude())    // 위도
                .longitude(request.getLongitude())  // 경도
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .showhosts(showhosts)
                .build();

        scheduleRepository.save(schedule);

        /*
         * 3. 일정 생성 알림
         * 쇼호스트에게 일정 배정 알림
         */
        for (Member showhost : showhosts) {
            notificationService.send(
                    showhost.getId(),
                    "새로운 일정이 배정되었습니다",
                    schedule.getTitle() + " (" + schedule.getStartTime() + ")",
                    NotificationType.SCHEDULE,
                    "/schedules/" + schedule.getId()
            );
        }

        // 4. 파일 업로드
        if (files != null && !files.isEmpty()) {

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : files) {

                try {
                    String originalName = file.getOriginalFilename();

                    if (originalName == null || originalName.isEmpty()) {
                        throw new RuntimeException("파일 이름이 없습니다.");
                    }

                    String savedName = UUID.randomUUID() + "_" + originalName;

                    File dest = new File(dir, savedName);
                    file.transferTo(dest);

                    FileEntity entity = FileEntity.builder()
                            .fileName(originalName)
                            .fileKey(savedName)
                            .fileUrl("/files/" + savedName)
                            .schedule(schedule)
                            .isPublic(true)
                            .build();

                    fileRepository.save(entity);

                    /*
                     * 파일 업로드 알림
                     */
                    for (Member showhost : showhosts) {
                        notificationService.send(
                                showhost.getId(),
                                "파일이 업로드되었습니다",
                                originalName + " (" + schedule.getTitle() + ")",
                                NotificationType.FILE,
                                "/schedules/" + schedule.getId()
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("파일 저장 실패");
                }
            }
        }
    }

    /*
     * 전체 일정 조회 (관리자)
     */
    public List<ScheduleResponseDto> getAllSchedules() {

        return scheduleRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    /*
     * 일정 상세 조회
     */
    public ScheduleResponseDto getSchedule(Long id) {

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정 없음"));

        return toDto(schedule);
    }

    /*
     * 쇼호스트 → 내 일정 조회
     */
    public List<ScheduleResponseDto> getMySchedules(Long memberId) {

        return scheduleRepository.findByShowhosts_Id(memberId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    /*
     * Entity → DTO 변환
     */
    private ScheduleResponseDto toDto(Schedule schedule) {

        return ScheduleResponseDto.from(schedule);
    }
}