package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@ToString
@Data
public class ProfileImageRequest {
    private String fileName;
    private String fileType;
    private Long fileSize;
    private MultipartFile file;


    public ProfileImageRequest(String fileName, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }
}
