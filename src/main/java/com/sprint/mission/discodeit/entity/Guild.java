package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.Permission;
import com.sprint.mission.discodeit.utility.StringUtil;
import com.sprint.mission.discodeit.utility.Validators;
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
  private final Map<UUID, Set<Permission>> members = new HashMap<>();
  private final List<Channel> channels = new ArrayList<>();
  private final Set<UUID> bans = new HashSet<>();

  private static final Set<Permission> DEFAULT_PERMISSIONS =
      EnumSet.of(Permission.READ_MESSAGES, Permission.SEND_MESSAGES);

  public Guild(String name, boolean discoverable, UUID ownerId) {
    this.name = StringUtil.normalizeString(name);
    this.discoverable = discoverable;
    this.ownerId = ownerId;
    addMember(ownerId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = Validators.validateGuildName(name);
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
    updateMemberPermissions(ownerId, Set.of(Permission.ADMINISTRATOR));
  }

  public boolean isOwner(UUID userId) {
    return userId.equals(ownerId);
  }

  public List<Channel> getChannels() {
    return Collections.unmodifiableList(channels);
  }

  public void addChannel(Channel channel) {
    if (channel == null) {
      throw new IllegalArgumentException("채널은 null일 수 없습니다.");
    }
    if (!channels.contains(channel)) {
      channels.add(channel);
    }
  }

  public void removeChannel(Channel channel) {
    channels.remove(channel);
  }

  public Map<UUID, Set<Permission>> getMembers() {
    return Collections.unmodifiableMap(members);
  }

  public void addMember(UUID userId) {
    if (userId == null) {
      throw new IllegalArgumentException("userId는 null일 수 없습니다.");
    }
    if (userId.equals(ownerId)) {
      members.putIfAbsent(userId, EnumSet.of(Permission.ADMINISTRATOR));
    } else {
      members.putIfAbsent(userId, EnumSet.copyOf(DEFAULT_PERMISSIONS));
    }
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

  public boolean isMember(UUID userId) {
    return members.containsKey(userId);
  }

  public boolean isBanned(UUID userId) {
    return bans.contains(userId);
  }

  @Override
  public String toString() {
    return "Guild{"
        + "name='"
        + name
        + '\''
        + ", discoverable="
        + discoverable
        + ", ownerId="
        + ownerId
        + ", members="
        + members
        + ", channels="
        + channels
        + ", bans="
        + bans
        + '}';
  }
}
