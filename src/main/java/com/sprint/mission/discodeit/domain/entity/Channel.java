package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Channel extends AbstractEntity {

  private String name;
  private String description;
  private final ChannelType type;

  private final Set<UUID> participantIds = new HashSet<>();
  private final Set<UUID> messageIds = new LinkedHashSet<>();

  public Channel(String name, String description) {
    Objects.requireNonNull(name, "Channel name must not be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Channel name must not be blank");
    }
    if (description != null && description.isBlank()) {
      description = null;
    }
    this.name = name;
    this.description = description;
    this.type = ChannelType.PUBLIC;
  }

  public Channel(Collection<UUID> participantIds) {
    this.participantIds.addAll(participantIds);
    this.type = ChannelType.PRIVATE;
  }

  public void changeName(String name) {
    Objects.requireNonNull(name, "Channel name must not be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Channel name must not be blank");
    }
    if (!Objects.equals(this.name, name)) {
      this.name = name;
      touch();
    }
  }

  public void changeDescription(String description) {
    if (!Objects.equals(this.description, description)) {
      this.description = description;
      touch();
    }
  }

  @Override
  public String toString() {
    return "Channel[id=%s, name=%s, type=%s, participants=%d]"
        .formatted(getId(), name, type, participantIds.size());
  }
}
