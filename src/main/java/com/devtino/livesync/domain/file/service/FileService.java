package com.devtino.livesync.domain.file.service;

import com.devtino.livesync.domain.file.domain.FileEntity;
import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.repository.FileRepository;
import com.devtino.livesync.domain.member.entity.Member;
import com.devtino.livesync.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    /*
     * DB 저장을 위한 Repository
     */
    private final FileRepository fileRepository;

    private final MemberRepository memberRepository;

    /*
     * 파일 업로드 경로 설정
     */
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    /*
     * 파일 업로드
     */
    public List<String> uploadFiles(List<MultipartFile> files, Long memberId){

        List<String> fileUrls = new ArrayList<>();

        // 1. 업로드 폴더 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // memberId → Member 객체 변환
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 2. 파일 반복 처리
        for (MultipartFile file : files) {

            try {
                String originalName = file.getOriginalFilename();

                if (originalName == null || originalName.isEmpty()) {
                    throw new RuntimeException("파일 이름이 없습니다.");
                }

                // 확장자 추출
                String ext = "";
                int index = originalName.lastIndexOf(".");
                if (index != -1) {
                    ext = originalName.substring(index);
                }

                // UUID 파일명
                String savedName = UUID.randomUUID() + ext;

                // 파일 저장
                File dest = new File(dir, savedName);
                file.transferTo(dest);

                // URL 생성
                String fileUrl = "/files/" + savedName;
                fileUrls.add(fileUrl);

                // DB 저장 (관리자 업로드 + 공개 파일)
                FileEntity entity = FileEntity.builder()
                        .fileName(originalName)
                        .fileUrl(fileUrl)
                        .fileKey(savedName)
                        .member(member)
                        .isPublic(true) // 쇼호스트에게 공개
                        .build();

                fileRepository.save(entity);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("파일 저장 실패");
            }
        }

        return fileUrls;
    }

    /*
     * 전체 파일 조회 (관리자용)
     */
    public List<FileResponseDto> getFileList() {

        List<FileEntity> entities = fileRepository.findAll();
        List<FileResponseDto> result = new ArrayList<>();

        for (FileEntity entity : entities) {
            result.add(
                    FileResponseDto.builder()
                            .id(entity.getId())
                            .fileName(entity.getFileName())
                            .fileUrl(entity.getFileUrl())
                            .build()
            );
        }

        return result;
    }

    /*
     * 공개 파일 조회 (쇼호스트용)
     */
    public List<FileResponseDto> getPublicFiles() {

        List<FileEntity> entities = fileRepository.findByIsPublicTrue();
        List<FileResponseDto> result = new ArrayList<>();

        for (FileEntity entity : entities) {
            result.add(
                    FileResponseDto.builder()
                            .id(entity.getId())
                            .fileName(entity.getFileName())
                            .fileUrl(entity.getFileUrl())
                            .build()
            );
        }

        return result;
    }

    /*
     * 파일 단건 조회
     */
    public FileEntity getFile(Long id) {

        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    /*
     * 파일 삭제 (관리자만)
     */
    public void deleteFile(Long id) {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        File physicalFile = new File(uploadDir, file.getFileKey());

        if (physicalFile.exists()) {
            physicalFile.delete();
        }

        fileRepository.delete(file);
    }
}