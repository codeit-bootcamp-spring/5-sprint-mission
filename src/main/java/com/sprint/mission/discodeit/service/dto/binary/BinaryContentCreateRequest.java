package com.sprint.mission.discodeit.service.dto.binary;

/**
 * 저장 요청 DTO
 * - data: 실제 바이너리
 * - contentType: MIME (e.g. image/png)
 * - fileName: 원본 파일명(선택)
 * - size: 길이(선택, 검증용)
 */
public class BinaryContentCreateRequest {
    private byte[] data;
    private String contentType;
    private String fileName;
    private Long size;

    public BinaryContentCreateRequest() { }

    public BinaryContentCreateRequest(byte[] data, String contentType, String fileName, Long size) {
        this.data = data;
        this.contentType = contentType;
        this.fileName = fileName;
        this.size = size;
    }

    public byte[] getData() { return data; }
    public String getContentType() { return contentType; }
    public String getFileName() { return fileName; }
    public Long getSize() { return size; }
}

