package com.devtino.livesync.domain.schedule.service;

import com.devtino.livesync.domain.file.service.FileService;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import com.devtino.livesync.domain.schedule.dto.ScheduleRequestDto;
import com.devtino.livesync.domain.schedule.dto.ScheduleResponseDto;
import com.devtino.livesync.domain.schedule.entity.Schedule;
import com.devtino.livesync.domain.schedule.entity.ScheduleAssignment;
import com.devtino.livesync.domain.schedule.repository.ScheduleAssignmentRepository;
import com.devtino.livesync.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleAssignmentRepository assignmentRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    // 일정 등록
    @Transactional
    public ScheduleResponseDto createSchedule(
            ScheduleRequestDto requestDto,
            List<MultipartFile> files,
            Long creatorId
    ) {
        Member creator = findMemberById(creatorId);

        Schedule schedule = Schedule.builder()
                .title(requestDto.getTitle())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .description(requestDto.getDescription())
                .createdBy(creator)
                .build();

        scheduleRepository.save(schedule);

        // 쇼호스트 배정
        assignHosts(schedule, requestDto.getHostIds());

        // 파일 첨부
        if (files != null && !files.isEmpty()) {
            fileService.uploadFilesToSchedule(files, creatorId, schedule);
        }

        return ScheduleResponseDto.from(schedule);
    }

    // 월별 일정 조회
    public List<ScheduleResponseDto> getSchedulesByMonth(int year, int month) {
        return scheduleRepository.findByYearAndMonth(year, month).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    // 일정 상세 조회
    public ScheduleResponseDto getSchedule(Long scheduleId) {
        return ScheduleResponseDto.from(findScheduleById(scheduleId));
    }

    // 일정 수정
    @Transactional
    public ScheduleResponseDto updateSchedule(
            Long scheduleId,
            ScheduleRequestDto requestDto,
            List<MultipartFile> files,
            Long memberId
    ) {
        Schedule schedule = findScheduleById(scheduleId);

        schedule.update(
                requestDto.getTitle(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getDescription()
        );

        // 쇼호스트 재배정
        assignmentRepository.deleteByScheduleId(scheduleId);
        schedule.getAssignments().clear();
        assignHosts(schedule, requestDto.getHostIds());

        // 파일 추가 업로드
        if (files != null && !files.isEmpty()) {
            fileService.uploadFilesToSchedule(files, memberId, schedule);
        }

        return ScheduleResponseDto.from(schedule);
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.delete(findScheduleById(scheduleId));
    }

    // 존재하지 않는 경우

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private void assignHosts(Schedule schedule, List<Long> hostIds) {
        if (hostIds == null || hostIds.isEmpty()) return;

        hostIds.forEach(hostId -> {
            Member host = findMemberById(hostId);

            ScheduleAssignment assignment = ScheduleAssignment.builder()
                    .schedule(schedule)
                    .member(host)
                    .appliedPay(null) // 단가 관리 구현 후 연동
                    .build();

            schedule.getAssignments().add(assignment);
            assignmentRepository.save(assignment);
        });
    }
}