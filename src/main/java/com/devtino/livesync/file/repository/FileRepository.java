package com.devtino.livesync.file.repository;

import com.devtino.livesync.file.domain.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 파일 DB 접근을 담당하는 Repository
 * JpaRepository를 상속받아 기본 CRUD 제공
 */
public interface FileRepository extends JpaRepository<FileEntity, Long> {
}