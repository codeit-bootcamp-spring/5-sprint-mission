package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.channel.ChannelType;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel extends AbstractBaseEntity {
  private final long createdAt;
  private long updatedAt;
  private String name;
  private ChannelType type;
  private final Set<UUID> joinedUsers;
  private boolean isPublic;

  public Channel(String name, ChannelType type, boolean isPublic) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.name = name;
    this.type = type;
    this.isPublic = isPublic;
    this.joinedUsers = new HashSet<>();
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

  public ChannelType getChannelType() {
    return type;
  }

  public void setChannelType(ChannelType type) {
    this.type = type;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Set<UUID> getJoinedUsers() {
    return joinedUsers;
  }

  public void addJoinedUser(UUID joinedUserId) {
    joinedUsers.add(joinedUserId);
  }

  public void removeJoinedUser(UUID joinedUserId) {
    joinedUsers.remove(joinedUserId);
  }

  public void clearJoinedUsers() {
    joinedUsers.clear();
  }

  @Override
  public String toString() {
    return "Channel{"
        + "id="
        + this.getId()
        + ", createdAt="
        + createdAt
        + ", joinedUsers="
        + joinedUsers
        + ", updatedAt="
        + updatedAt
        + ", name='"
        + name
        + '\''
        + ", type="
        + type
        + ", isPublic="
        + isPublic
        + '}';
  }
}
