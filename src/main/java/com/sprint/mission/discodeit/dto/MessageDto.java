package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record MessageDto() {

    public record CreateMessage(
            UUID channelId,
            UUID senderId,
            String content,
            List<byte[]> attachments
    ) {}
}
