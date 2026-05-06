package com.devtino.livesync.file.service;

import com.devtino.livesync.file.domain.FileEntity;
import com.devtino.livesync.file.dto.FileResponseDto;
import com.devtino.livesync.file.repository.FileRepository;
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

    /*
     * 파일 업로드 경로 설정
     * - System.getProperty("user.dir") : 현재 프로젝트 실행 위치 (절대경로)
     * - 상대경로("uploads") 사용 시 Tomcat temp 경로로 저장되는 문제 발생
     */
    private final String uploadDir = System.getProperty("user.dir") + "/uploads";

    /*
     * 여러 파일 업로드 처리
     *
     * @param files 업로드된 파일 리스트 (Multipart 형식)
     * @return 저장된 파일들의 접근 URL 리스트
     */
    public List<String> uploadFiles(List<MultipartFile> files) {

        // 반환할 URL 리스트
        List<String> fileUrls = new ArrayList<>();

        /*
         * 1. 업로드 폴더 생성
         * - 서버 실행 시 해당 폴더가 없으면 자동 생성
         * - 이미 존재하면 생성 안 함
         */
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        /*
         * 2. 파일 반복 처리 (여러 개 업로드 대응)
         */
        for (MultipartFile file : files) {

            try {
                /*
                 * 3. 원본 파일명 가져오기
                 * - null 가능성 있음 → 반드시 체크
                 */
                String originalName = file.getOriginalFilename();

                if (originalName == null || originalName.isEmpty()) {
                    throw new RuntimeException("파일 이름이 없습니다.");
                }

                /*
                 * 4. 파일 확장자 추출
                 * - "."이 없는 경우 대비
                 */
                String ext = "";
                int index = originalName.lastIndexOf(".");
                if (index != -1) {
                    ext = originalName.substring(index);
                }

                /*
                 * 5. 파일명 중복 방지 (UUID 사용)
                 * - 실제 저장되는 파일명은 랜덤값으로 변경
                 */
                String savedName = UUID.randomUUID() + ext;

                /*
                 * 6. 저장할 파일 경로 생성
                 * - uploadDir(절대경로) + 파일명
                 */
                File dest = new File(dir, savedName);

                /*
                 * 7. 파일 저장
                 * - MultipartFile → 실제 파일로 변환
                 */
                file.transferTo(dest);

                /*
                 * 8. 클라이언트 접근 URL 생성
                 * - 실제 파일 위치가 아니라 다운로드 API 기준 URL
                 */
                String fileUrl = "/files/" + savedName;
                fileUrls.add(fileUrl);

                /*
                 * 9. DB 저장 추가
                 * - 파일 메타데이터를 DB에 저장
                 */
                FileEntity entity = FileEntity.builder()
                        .fileName(originalName)
                        .fileUrl(fileUrl)
                        .fileKey(savedName)
                        .build();

                fileRepository.save(entity);

            } catch (Exception e) {

                /*
                 * 10. 예외 처리
                 * - 로그 출력 후 런타임 예외 발생
                 */
                e.printStackTrace();
                throw new RuntimeException("파일 저장 실패");
            }
        }

        /*
         * 11. 저장된 파일 URL 리스트 반환
         */
        return fileUrls;
    }

    /*
     * 업로드된 파일 목록 조회
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
     * 파일 단건 조회 (id 기반)
     */
    public FileEntity getFile(Long id) {

        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    /*
     * 파일 삭제 (id 기반)
     */
    public void deleteFile(Long id) {

        // 1. DB에서 파일 조회
        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));

        /*
         * 2. 실제 파일 경로 생성
         * - 저장된 fileKey를 이용해 서버 파일 찾기
         */
        File physicalFile = new File(uploadDir, file.getFileKey());

        /*
         * 3. 실제 파일 삭제
         * - 파일이 존재할 때만 삭제
         */
        if (physicalFile.exists()) {
            physicalFile.delete();
        }

        /*
         * 4. DB 데이터 삭제
         */
        fileRepository.delete(file);
    }
}