package com.sprint.mission.discodeit.dto;

public record CreateFile(
        String fileName,
        String fileType,
        byte[] data,
        Long fileSize
) {}
