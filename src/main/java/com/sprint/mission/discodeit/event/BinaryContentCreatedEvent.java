package com.sprint.mission.discodeit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BinaryContentCreatedEvent {
    private final UUID binaryContentId;
    private final UUID receiverId;
    private final byte[] bytes;
}