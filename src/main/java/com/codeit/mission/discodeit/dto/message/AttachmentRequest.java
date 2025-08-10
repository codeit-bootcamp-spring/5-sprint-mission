package com.codeit.mission.discodeit.dto.message;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AttachmentRequest {

    private final String fileName;
    private final String contentType;
    private final byte[] bytes;

    public AttachmentRequest(String fileName, String contentType, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public Long getSize() {
        return bytes != null ? (long) bytes.length : 0L;
    }
}
