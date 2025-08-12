package com.sprint.mission.discodeit.dto.binaryContent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@RequiredArgsConstructor
public class BinaryContentCreateRequest {
    private final String fileName;
    private final String contentType;
    private final Long size;
    private final byte[] bytes;
    private final UUID ownerId;
}
