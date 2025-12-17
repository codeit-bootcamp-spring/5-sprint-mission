package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.time.Instant;
import lombok.Getter;

@Getter
public class BinaryContentCreatedEvent extends CreatedEvent<BinaryContent> {

    private final byte[] bytes;

    public BinaryContentCreatedEvent(BinaryContent binaryContent, Instant createdAt, byte[] bytes) {
        super(binaryContent, createdAt);
        this.bytes = bytes;
    }
}
