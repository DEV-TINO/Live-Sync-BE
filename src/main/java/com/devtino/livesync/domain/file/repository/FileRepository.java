package com.devtino.livesync.domain.file.repository;

import com.devtino.livesync.domain.file.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 파일 DB 접근을 담당하는 Repository
 * JpaRepository를 상속받아 기본 CRUD 제공
 */
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    // 특정 사용자가 업로드한 파일 조회
    List<FileEntity> findByMemberId(Long memberId);

    // 공개된 파일 조회 (쇼호스트용)
    List<FileEntity> findByIsPublicTrue();
}