package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record MessageCreateRequest(
    String content,
    UUID channelId,
    UUID authorId
) {
  public static MessageCreateRequest of(String content, UUID channelId, UUID authorId) {
    return new MessageCreateRequest(content, channelId, authorId);
  }

  public MessageCreateRequest withChannelId(UUID newChannelId) {
    return new MessageCreateRequest(this.content, newChannelId, this.authorId);
  }
}

