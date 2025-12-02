package com.sprint.mission.discodeit.dto.response.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Base64BinaryContentResponse {
    private UUID id;
    private Instant createdAt;
    private String fileName;
    private String contentType;
    private Long size;
    private BinaryContentStatus status;
    private String bytes;
    private boolean success;

    private Base64BinaryContentResponse(BinaryContent binaryContent, byte[] bytes) {
        this.id = binaryContent.getId();
        this.createdAt = binaryContent.getCreatedAt();
        this.fileName = binaryContent.getFileName();
        this.contentType = binaryContent.getContentType();
        this.size = binaryContent.getSize();
        this.status = binaryContent.getStatus();
        this.bytes = Base64.getEncoder().encodeToString(bytes);
        this.success = true;
    }

    public static Base64BinaryContentResponse fromResponse(BinaryContentResponse response) {
        return Base64BinaryContentResponse.builder()
                .id(response.getId())
                .createdAt(response.getCreatedAt())
                .fileName(response.getFileName())
                .contentType(response.getContentType())
                .size(response.getSize())
                .status(response.getStatus())
                .bytes(Base64.getEncoder().encodeToString(response.getBytes()))
                .success(response.isSuccess())
                .build();
    }

    @Override
    public String toString() {
        return "Base64BinaryContentResponse(" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", fileName=" + fileName +
                ", contentType=" + contentType +
                ", size=" + size +
                ", status=" + status +
                ')';
    }
}