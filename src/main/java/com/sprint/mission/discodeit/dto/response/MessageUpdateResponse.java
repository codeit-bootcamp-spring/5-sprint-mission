package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageUpdateResponse(
        UUID id,
        UUID authorId,
        UUID channelId,
        String content,
        Instant updatedAt
) {
    // 엔터티 -> DTO 변환 생성자
    public MessageUpdateResponse(Message message) {
        this(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent(),
                message.getUpdatedAt()
        );
    }
}
