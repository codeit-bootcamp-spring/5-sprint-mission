package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.channelentity.ChannelCategory;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel {
  private final UUID id;
  private final long createdAt;
  private final Set<UUID> joinedUsers;
  private long updatedAt;
  private String name;
  private String groupName;
  private ChannelCategory category;
  private boolean isPublic;

  public Channel(String name, String groupName, ChannelCategory category, boolean isPublic) {
    this.id = UUID.randomUUID();
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.name = name;
    this.groupName = groupName;
    this.category = category;
    this.isPublic = isPublic;
    this.joinedUsers = new HashSet<>();
  }

  public UUID getId() {
    return id;
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

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public ChannelCategory getCategory() {
    return category;
  }

  public void setCategory(ChannelCategory category) {
    this.category = category;
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
        + "name='"
        + name
        + '\''
        + ", groupName='"
        + groupName
        + '\''
        + ", category="
        + category
        + ", isPublic="
        + isPublic
        + ", joinedUsers="
        + joinedUsers
        + '}';
  }
}
