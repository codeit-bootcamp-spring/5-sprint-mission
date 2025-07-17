package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.enums.serverentity.ServerLevel;
import com.sprint.mission.discodeit.enums.serverentity.ServerPerk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Server extends AbstractBaseEntity {
  private final long createdAt;
  private final List<Channel> channels;
  private final Set<UUID> members;
  private final Set<ServerPerk> perks;
  private long updatedAt;
  private String name;
  private UUID ownerId;
  private boolean isPublic;
  private long boost;
  private ServerLevel level;

  public Server(
      String name, UUID ownerId, boolean isPublic, Set<UUID> membersId, List<Channel> channels) {
    this.createdAt = System.currentTimeMillis();
    this.updatedAt = this.createdAt;
    this.name = name;
    this.ownerId = ownerId;
    this.isPublic = isPublic;
    this.members = new HashSet<>(membersId);
    this.channels = new ArrayList<>(channels);
    this.level = ServerLevel.ONE;
    this.perks = new HashSet<>();
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

  public void addMember(UUID memberId) {
    members.add(memberId);
  }

  public void removeMember(UUID memberId) {
    members.remove(memberId);
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

  public long getBoost() {
    return boost;
  }

  public void setBoost(long boost) {
    this.boost = boost;
  }

  public ServerLevel getLevel() {
    return level;
  }

  public void setLevel(ServerLevel level) {
    this.level = level;
  }

  public Set<ServerPerk> getPerks() {
    return Collections.unmodifiableSet(perks);
  }

  public void addPerk(ServerPerk perk) {
    perks.add(perk);
  }

  public void removePerk(ServerPerk perk) {
    perks.remove(perk);
  }

  public void clearPerks() {
    perks.clear();
  }

  @Override
  public String toString() {
    return "Server{"
        + "id="
        + this.getId()
        + ", createdAt="
        + createdAt
        + ", channels="
        + channels
        + ", members="
        + members
        + ", perks="
        + perks
        + ", updatedAt="
        + updatedAt
        + ", name='"
        + name
        + '\''
        + ", ownerId="
        + ownerId
        + ", isPublic="
        + isPublic
        + ", boost="
        + boost
        + ", level="
        + level
        + '}';
  }
}
