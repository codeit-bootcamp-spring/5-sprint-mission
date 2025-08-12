package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class ProfileImageRequest {
    private String fileName;
    private String fileType;
    private Long fileSize;

    public ProfileImageRequest(String fileName, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
