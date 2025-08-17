package com.sprint.mission.discodeit.dto.binary;

/**
 * 저장 요청 DTO
 */
public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] bytes
) {
}

