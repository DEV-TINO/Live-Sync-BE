package com.devtino.livesync.domain.file.repository;

import com.devtino.livesync.domain.file.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * 파일 DB 접근을 담당하는 Repository
 * JpaRepository를 상속받아 기본 CRUD 제공
 */
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    // 특정 사용자가 업로드한 파일 조회
    List<FileEntity> findByMemberId(Long memberId);

    // 공개된 파일 조회 (쇼호스트용)
    List<FileEntity> findByIsPublicTrue();

    // 일정별 파일 조회
    List<FileEntity> findByScheduleId(Long scheduleId);

    // 일정별 최신 파일만 조회
    List<FileEntity> findByScheduleIdAndIsLatestTrue(Long scheduleId);

    // 일정 내 동일 파일명 최신 버전 조회 (버전 관리용)
    Optional<FileEntity> findByScheduleIdAndFileNameAndIsLatestTrue(
            Long scheduleId, String fileName
    );

}