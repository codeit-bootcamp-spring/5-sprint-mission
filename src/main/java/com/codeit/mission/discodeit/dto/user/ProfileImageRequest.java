package com.codeit.mission.discodeit.dto.user;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProfileImageRequest {

    private final String fileName;
    private final String contentType;
    private final Long size;
    private final byte[] bytes;

    public ProfileImageRequest(String fileName, String contentType, Long size, byte[] bytes) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.bytes = bytes;
    }
}
