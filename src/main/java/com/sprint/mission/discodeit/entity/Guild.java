package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Guild extends AbstractBaseEntity {
  private final long createdAt;
  private long updatedAt;
  private boolean isPublic;
  private UUID ownerId;
  private String name;
  private final Set<UUID> members;
  private final List<Channel> channels;

  public Guild(boolean isPublic, UUID ownerId, String name) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.isPublic = isPublic;
    this.ownerId = ownerId;
    this.name = name;
    this.members = new HashSet<>();
    this.channels = new ArrayList<>();
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(long updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public List<Channel> getChannels() {
    return Collections.unmodifiableList(channels);
  }

  public void addChannel(Channel channel) {
    channels.add(channel);
  }

  public void removeChannel(Channel channel) {
    channels.remove(channel);
  }

  public void clearChannels() {
    channels.clear();
  }

  public Set<UUID> getMembers() {
    return Collections.unmodifiableSet(members);
  }

  public void addMember(UUID member) {
    members.add(member);
  }

  public void removeMember(UUID member) {
    members.remove(member);
  }

  public void clearMembers() {
    members.clear();
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
        + this.getId()
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + ", isPublic="
        + isPublic
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
