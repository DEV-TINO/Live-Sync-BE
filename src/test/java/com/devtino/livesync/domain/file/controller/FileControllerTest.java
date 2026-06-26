package com.devtino.livesync.domain.file.controller;

import com.devtino.livesync.domain.file.dto.FileDownload;
import com.devtino.livesync.domain.file.dto.FileResponseDto;
import com.devtino.livesync.domain.file.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FileControllerTest {

    private final FileService fileService = mock(FileService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new FileController(fileService))
            .build();

    @Test
    void uploadFilesReturnsUploadedUrls() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "files",
                "test.txt",
                "text/plain",
                "hello".getBytes()
        );
        when(fileService.uploadFiles(anyList(), any()))
                .thenReturn(List.of("/files/download/10"));

        mockMvc.perform(multipart("/files/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("/files/download/10"));
    }

    @Test
    void getFilesReturnsFileList() throws Exception {
        when(fileService.getFileList()).thenReturn(List.of(
                FileResponseDto.builder()
                        .id(1L)
                        .fileName("guide.pdf")
                        .fileUrl("/files/download/1")
                        .build()
        ));

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("guide.pdf"))
                .andExpect(jsonPath("$[0].fileUrl").value("/files/download/1"));
    }

    @Test
    void downloadByIdReturnsAttachment() throws Exception {
        when(fileService.downloadFile(1L)).thenReturn(new FileDownload(
                "guide.pdf",
                new ByteArrayResource("pdf".getBytes())
        ));

        mockMvc.perform(get("/files/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("guide.pdf")))
                .andExpect(content().bytes("pdf".getBytes()));
    }

    @Test
    void deleteFileDeletesById() throws Exception {
        mockMvc.perform(delete("/files/1"))
                .andExpect(status().isOk());

        verify(fileService).deleteFile(1L);
    }
}
