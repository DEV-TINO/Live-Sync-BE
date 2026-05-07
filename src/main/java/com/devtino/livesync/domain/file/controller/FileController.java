package com.devtino.livesync.domain.file.controller;

import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// Swagger용
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 업로드 및 다운로드 API")
public class FileController {

    private final FileService fileService;

    /*
     * 파일 업로드 (관리자만 가능)
     */
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "파일 업로드", description = "여러 개의 파일을 업로드합니다.")
    public List<String> uploadFiles(
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal Long memberId
    ) {
        return fileService.uploadFiles(files, memberId);
    }

    /*
     * 전체 파일 조회 (관리자용)
     */
    @GetMapping
    @Operation(summary = "파일 목록 조회", description = "전체 파일 조회 (관리자)")
    public List<FileResponseDto> getFiles() {
        return fileService.getFileList();
    }

    /*
     * 쇼호스트용 파일 조회 (관리자가 올린 공개 파일)
     */
    @GetMapping("/showhost")
    @Operation(summary = "쇼호스트 파일 조회", description = "관리자가 업로드한 공개 파일 조회")
    public List<FileResponseDto> getPublicFiles() {
        return fileService.getPublicFiles();
    }

    /*
     * 파일 다운로드
     */
    @GetMapping("/download/{id}")
    @Operation(summary = "파일 다운로드", description = "파일 ID를 통해 파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadById(@PathVariable Long id) throws Exception {

        var file = fileService.getFile(id);

        Path path = Paths.get(System.getProperty("user.dir"), "uploads", file.getFileKey());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }

    /*
     * 파일 삭제 (관리자만)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "파일 삭제", description = "파일 ID를 통해 파일을 삭제합니다.")
    public String deleteFile(@PathVariable Long id) {

        fileService.deleteFile(id);

        return "삭제 완료";
    }
}