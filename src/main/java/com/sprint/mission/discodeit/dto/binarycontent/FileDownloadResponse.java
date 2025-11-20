package com.sprint.mission.discodeit.dto.binarycontent;

import org.springframework.core.io.Resource;

public record FileDownloadResponse(
    Resource resource,
    String fileName,
    String contentType,
    long size
) {
}
