package com.sprint.mission.discodeit.dto.response.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sprint.mission.discodeit.domain.entity.Message;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record MessageResponse(

    UUID id,
    Instant createdAt,
    Instant updatedAt,
    UUID channelId,
    UUID authorId,

    @JsonInclude(JsonInclude.Include.ALWAYS)
    String content,
    Set<UUID> attachmentIds
) {

  public static MessageResponse from(Message m) {
    return new MessageResponse(
        m.getId(),
        m.getCreatedAt(),
        m.getUpdatedAt(),
        m.getChannelId(),
        m.getAuthorId(),
        m.getContent(),
        m.getAttachmentIds()
    );
  }
}
