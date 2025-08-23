package com.sprint.mission.discodeit.domain.entity;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends AbstractEntity {

  private final UUID channelId;
  private final UUID authorId;

  private String content;
  private final Set<UUID> attachmentIds = new LinkedHashSet<>();

  public Message(UUID channelId, UUID authorId, String content, Set<UUID> attachmentIds) {
    this.channelId = Objects.requireNonNull(channelId, "channelId must not be null.");
    this.authorId = Objects.requireNonNull(authorId, "authorId must not be null.");
    if (content == null || content.isBlank()) {
      this.content = null;
    } else {
      this.content = content;
    }
    for (UUID id : attachmentIds) {
      this.attachmentIds.add(Objects.requireNonNull(id, "attachmentId must not be null"));
    }
  }

  public Message update(String content) {
    if (content == null || content.isBlank()) {
      this.content = null;
    } else {
      this.content = content;
    }

    return this;
  }

  public Set<UUID> getAttachmentIds() {
    return Collections.unmodifiableSet(attachmentIds);
  }

  @Override
  public String toString() {
    return "Message[channel=%s, author=%s, content='%s', attachments=%d]"
        .formatted(channelId, authorId, content, attachmentIds.size());
  }
}
