package com.sprint.mission.discodeit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BinaryContentCreateRequest {
    private String fileName;
    private String contentType;
    private Long size;
    private byte[] bytes;
    private UUID userId;
    private UUID messageId;
}
