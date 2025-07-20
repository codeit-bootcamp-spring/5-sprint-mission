package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Guild extends BaseEntity {
  private String name;
  private boolean isDiscoverable;
  private UUID ownerId;
  private final Set<UUID> members;
  private final List<Channel> channels;

  public Guild(String name, boolean isPublic, UUID ownerId) {
    this.name = name == null ? "" : name.strip();
    this.isDiscoverable = isPublic;
    this.ownerId = ownerId;
    this.members = new HashSet<>();
    this.channels = new ArrayList<>();
  }

  public String getName() {
    if (name == null) {
      return "";
    }
    return name;
  }

  public void setName(String name) {
    if (name != null) {
      this.name = name.strip();
    }
  }

  public boolean isDiscoverable() {
    return isDiscoverable;
  }

  public void setDiscoverable(boolean isPublic) {
    this.isDiscoverable = isPublic;
  }

  public List<Channel> getChannels() {
    return Collections.unmodifiableList(channels);
  }

  public void addChannel(Channel channel) {
    if (channel != null) {
      channels.add(channel);
    }
  }

  public void removeChannel(Channel channel) {
    channels.remove(channel);
  }

  public Set<UUID> getMembers() {
    return Collections.unmodifiableSet(members);
  }

  public void addMember(UUID member) {
    if (member != null) {
      members.add(member);
    }
  }

  public void removeMember(UUID member) {
    members.remove(member);
  }

  public UUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(UUID ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public String toString() {
    return "Guild{"
        + "id="
        + getId()
        + ", createdAt="
        + getCreatedAt()
        + ", updatedAt="
        + getUpdatedAt()
        + ", isPublic="
        + isDiscoverable
        + ", ownerId="
        + ownerId
        + ", name='"
        + name
        + '\''
        + ", members="
        + members
        + ", channels="
        + channels
        + '}';
  }
}
