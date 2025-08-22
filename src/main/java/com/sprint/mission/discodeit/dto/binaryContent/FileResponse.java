package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Builder;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Builder
public record FileResponse(
        UUID id,
        Instant createdAt,
        String fileName,
        String contentType,
        long size,
        String bytes
) {
    public static FileResponse of(BinaryContent file) {
        return FileResponse.builder()
                .id(file.getId())
                .createdAt(file.getCreatedAt())
                .fileName(file.getFileName())
                .size(file.getContent().length)
                .contentType(file.getContentType())
                .bytes(Base64.getEncoder().encodeToString(file.getContent()))
                .build();
    }
}
