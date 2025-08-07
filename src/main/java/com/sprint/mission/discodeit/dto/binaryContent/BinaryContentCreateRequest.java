package com.sprint.mission.discodeit.dto.binaryContent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentCreateRequest {
    private final byte[] data;
    private final String contentType;
}
