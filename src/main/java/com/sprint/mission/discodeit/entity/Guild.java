package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.utility.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Guild extends BaseEntity {
  private String name;
  private boolean discoverable;
  private UUID ownerId;
  private final Map<UUID, Set<Permission>> members;
  private final List<Channel> channels;
  private final Set<UUID> bans;

  private static final Set<Permission> DEFAULT_PERMISSIONS =
      EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES);

  public Guild(String name, boolean discoverable, UUID ownerId) {
    this.name = StringUtil.normalizeString(name);
    this.discoverable = discoverable;
    this.ownerId = ownerId;
    this.members = new HashMap<>();
    this.channels = new ArrayList<>();
    this.bans = new HashSet<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = StringUtil.normalizeString(name);
  }

  public boolean isDiscoverable() {
    return discoverable;
  }

  public void setDiscoverable(boolean discoverable) {
    this.discoverable = discoverable;
  }

  public UUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(UUID ownerId) {
    this.ownerId = ownerId;
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

  public Map<UUID, Set<Permission>> getMembers() {
    return Collections.unmodifiableMap(members);
  }

  public void addMember(UUID userId) {
    members.put(userId, EnumSet.copyOf(DEFAULT_PERMISSIONS));
  }

  public void updateMemberPermissions(UUID userId, Set<Permission> permissions) {
    members.put(userId, permissions);
  }

  public void removeMember(UUID userId) {
    members.remove(userId);
  }

  public Set<UUID> getBans() {
    return Collections.unmodifiableSet(bans);
  }

  public void addBan(UUID userId) {
    bans.add(userId);
  }

  public void removeBan(UUID userId) {
    bans.remove(userId);
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
        + ", discoverable="
        + discoverable
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
