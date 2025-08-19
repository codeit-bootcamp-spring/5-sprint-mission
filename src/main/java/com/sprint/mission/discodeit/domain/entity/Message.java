package com.sprint.mission.discodeit.domain.entity;

import java.util.Collection;
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
  private final UUID replyTo;

  private String content;
  private final Set<UUID> attachmentIds = new LinkedHashSet<>();

  public Message(UUID channelId, UUID authorId, String content, Set<UUID> attachmentIds,
      UUID replyTo) {
    this.channelId = Objects.requireNonNull(channelId, "channelId must not be null.");
    this.authorId = Objects.requireNonNull(authorId, "authorId must not be null.");

    this.content = content;
    if (attachmentIds != null) {
      for (UUID id : attachmentIds) {
        this.attachmentIds.add(Objects.requireNonNull(id, "attachmentId must not be null"));
      }
    }

    if (replyTo != null && replyTo.equals(getId())) {
      throw new IllegalArgumentException("Message cannot reply to itself.");
    }
    this.replyTo = replyTo;
  }

  public Message(UUID channelId, UUID authorId, String content, Set<UUID> attachmentIds) {
    this(channelId, authorId, content, attachmentIds, null);
  }

  public Message(UUID channelId, UUID authorId, String content) {
    this(channelId, authorId, content, new LinkedHashSet<>(), null);
  }

  public void setContent(String content) {
    if (!Objects.equals(this.content, content)) {
      this.content = content;
      touch();
    }
  }

  public Set<UUID> getAttachmentIds() {
    return Collections.unmodifiableSet(attachmentIds);
  }

  public void setAttachmentIds(Set<UUID> attachmentIds) {
    Set<UUID> target = (attachmentIds == null) ? Set.of() : attachmentIds;
    if (target.contains(null)) {
      throw new NullPointerException("attachmentId must not be null");
    }

    if (!this.attachmentIds.equals(target)) {
      this.attachmentIds.clear();
      this.attachmentIds.addAll(target);
      touch();
    }
  }

  public boolean addAttachmentId(UUID id) {
    Objects.requireNonNull(id, "attachmentId must not be null");
    boolean added = this.attachmentIds.add(id);
    if (added) {
      touch();
    }
    return added;
  }

  public int addAttachmentIds(Collection<UUID> ids) {
    Objects.requireNonNull(ids, "ids must not be null");
    int before = attachmentIds.size();
    for (UUID id : ids) {
      this.attachmentIds.add(Objects.requireNonNull(id, "attachmentId must not be null"));
    }
    int changed = attachmentIds.size() - before;
    if (changed > 0) {
      touch();
    }
    return changed;
  }

  public boolean removeAttachmentId(UUID id) {
    Objects.requireNonNull(id, "attachmentId must not be null");
    boolean removed = this.attachmentIds.remove(id);
    if (removed) {
      touch();
    }
    return removed;
  }

  public int removeAttachmentIds(Collection<UUID> ids) {
    Objects.requireNonNull(ids, "ids must not be null");
    int removed = 0;
    for (UUID id : ids) {
      if (this.attachmentIds.remove(Objects.requireNonNull(id, "attachmentId must not be null"))) {
        removed++;
      }
    }
    if (removed > 0) {
      touch();
    }
    return removed;
  }

  @Override
  public String toString() {
    return "Message[channel=%s, author=%s, content='%s', attachments=%d]"
        .formatted(channelId, authorId, content, attachmentIds.size());
  }
}
