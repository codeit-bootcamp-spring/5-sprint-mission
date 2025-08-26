package com.sprint.mission.discodeit.domain.entity;

import static com.sprint.mission.discodeit.support.StringUtil.blankOrStrip;
import static com.sprint.mission.discodeit.support.StringUtil.requireNonBlank;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel extends AbstractEntity {

  private String name;
  private String description;
  private final ChannelType type;

  private final Set<UUID> participantIds = new HashSet<>();
  private final Deque<UUID> messageIds = new ArrayDeque<>();

  public Channel(String name, String description) {
    this.name = requireNonBlank(name, "name must not be blank");
    this.description = blankOrStrip(description);
    this.type = ChannelType.PUBLIC;
  }

  public Channel(Collection<UUID> participantIds) {
    this.participantIds.addAll(participantIds);
    this.type = ChannelType.PRIVATE;
  }

  public Channel update(String newName, String newDescription) {
    if (type == ChannelType.PRIVATE) {
      throw new IllegalArgumentException("Cannot update private channel");
    }

    boolean changed = false;
    if (newName != null && !newName.isBlank() && !newName.equals(this.name)) {
      this.name = newName;
      changed = true;
    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;
      changed = true;
    }
    if (changed) {
      touch();
    }
    return this;
  }

  public Set<UUID> getParticipantIds() {
    return Collections.unmodifiableSet(participantIds);
  }

  public Optional<UUID> getLastMessageId() {
    if (messageIds.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(messageIds.peekLast());
  }

  public void addMessageId(UUID messageId) {
    messageIds.offer(messageId);
  }

  @Override
  public String toString() {
    return "Channel[id=%s, name=%s, type=%s, participants=%d]"
        .formatted(getId(), name, type, participantIds.size());
  }
}
