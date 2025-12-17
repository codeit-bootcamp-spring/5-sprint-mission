package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BinaryContentCreatedEvent {
    private final UUID binaryContentId;
    private final byte[] bytes;
}
