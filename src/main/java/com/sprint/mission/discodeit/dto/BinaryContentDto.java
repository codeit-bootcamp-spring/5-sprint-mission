package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BinaryContentDto {
    private String fileName;
    private String contentType;
    private byte[] content;
    private long fileSize;
}
