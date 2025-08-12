package com.sprint.mission.discodeit.service.dto.binary;

import java.util.UUID;

/**
 * 조회/응답 DTO
 * - 본문 바이트는 별도 다운로드 API로 제공하는 것을 권장
 */
public class BinaryContentResponse {
    private UUID id;
    private String contentType;
    private String fileName;
    private Long size;

    public BinaryContentResponse(UUID id, String contentType, String fileName, Long size) {
        this.id = id;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }

    public UUID getId() { return id; }
    public String getContentType() { return contentType; }
    public String getFileName() { return fileName; }
    public Long getSize() { return size; }
}
