package com.devtino.livesync.domain.file.dto;

import org.springframework.core.io.Resource;

public record FileDownload(String fileName, Resource resource) {
}
