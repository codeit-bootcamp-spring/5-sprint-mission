package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public record MessageFindResponse(
        UUID messageId,
        UUID authorId,
        UUID channelId,
        String content
) {
    // 엔터티 -> DTO 변환 생성자
    public MessageFindResponse(Message message) {
        this(
                message.getId(),
                message.getAuthorId(),
                message.getChannelId(),
                message.getContent()
        );
    }
}
