package com.sprint.mission.discodeit.dto.binarycontent;

public record FileUploadDto(
        String fileName,
        String contentType,
        byte[] content,
        long fileSize
) {}
