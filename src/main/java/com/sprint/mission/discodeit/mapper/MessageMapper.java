package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.Message;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageMapper {

    public static MessageResponse toMessageResponse(Message m) {
        if (m == null) return null;
        return new MessageResponse(
                m.getId(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getChannelId(),
                m.getAuthorId(),
                m.getContent(),
                m.getAttachmentIds(),
                m.getReplyTo()
        );
    }

    public static List<MessageResponse> toMessageResponses(Collection<Message> list) {
        if (list == null || list.isEmpty()) return List.of();
        return list.stream().map(MessageMapper::toMessageResponse).toList();
    }
}
