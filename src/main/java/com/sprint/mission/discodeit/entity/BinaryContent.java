package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Schema(name = "BinaryContent")
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "BinaryContent ID", format = "uuid")
    private UUID id;
    @Schema(description = "생성 시각", format = "date-time")
    private Instant createdAt;
    //
    @Schema(description = "파일명")
    private String fileName;
    @Schema(description = "파일 크기(Byte)", format = "int64")
    private Long size;
    @Schema(description = "콘텐츠 타입")
    private String contentType;
    @Schema(description = "파일 바이트", type = "string", format = "byte")
    private byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }
}
