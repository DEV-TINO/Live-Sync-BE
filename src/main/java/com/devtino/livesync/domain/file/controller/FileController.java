package com.devtino.livesync.domain.file.controller;

import com.devtino.livesync.domain.file.dto.FileDownload;
import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 업로드 및 다운로드 API")
public class FileController {

    private final FileService fileService;

    @GetMapping
    @Operation(summary = "파일 목록 조회", description = "관리자가 전체 파일 목록을 조회합니다.")
    public List<FileResponseDto> getFiles() {
        return fileService.getFileList();
    }

    @GetMapping("/showhost")
    @Operation(summary = "쇼호스트 파일 조회", description = "공개된 파일 목록을 조회합니다.")
    public List<FileResponseDto> getPublicFiles() {
        return fileService.getPublicFiles();
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "파일 다운로드", description = "파일 ID를 통해 파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadById(@PathVariable Long id) {
        FileDownload download = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(download.fileName(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(download.resource());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "파일 삭제", description = "관리자가 파일 ID를 통해 파일을 삭제합니다.")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok("삭제 완료");
    }
}
