package com.sprint.mission.discodeit.domain.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AbstractEntity {

  private String email;
  private String username;
  private String password;
  private UUID profileId;

  private final Set<UUID> friendIds = new HashSet<>();
  private final Set<UUID> guildIds = new HashSet<>();
  private final Set<UUID> channelIds = new HashSet<>();

  public User(
      String email,
      String username,
      String password,
      UUID profileId
  ) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email cannot be null or blank");
    }
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("Username cannot be null or blank");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password cannot be null or blank");
    }
    this.email = email;
    this.username = username;
    this.password = password;
    this.profileId = profileId;
  }

  public User update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
    boolean changed = false;
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
      changed = true;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
      changed = true;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
      changed = true;
    }
    if (newProfileId != null && !newProfileId.equals(this.profileId)) {
      this.profileId = newProfileId;
      changed = true;
    }
    if (changed) {
      touch();
    }
    return this;
  }

  public void removeProfileId() {
    if (this.profileId != null) {
      this.profileId = null;
      touch();
    }
  }

  public Set<UUID> getFriendIds() {
    return Collections.unmodifiableSet(friendIds);
  }

  public void addFriend(UUID friend) {
    Objects.requireNonNull(friend, "friend must not be null");
    if (friend.equals(getId())) {
      throw new IllegalArgumentException("Cannot add self as friend");
    }
    if (friendIds.add(friend)) {
      touch();
    }
  }

  public void removeFriend(UUID friend) {
    if (friendIds.remove(friend)) {
      touch();
    }
  }

  public boolean isFriend(UUID friend) {
    return friendIds.contains(friend);
  }

  public Set<UUID> getGuildIds() {
    return Collections.unmodifiableSet(guildIds);
  }

  public void joinGuild(UUID guildId) {
    Objects.requireNonNull(guildId, "guildId must not be null");
    if (guildIds.add(guildId)) {
      touch();
    }
  }

  public void leaveGuild(UUID guildId) {
    if (guildIds.remove(guildId)) {
      touch();
    }
  }

  public boolean isMemberOfGuild(UUID guildId) {
    return guildIds.contains(guildId);
  }

  public Set<UUID> getChannelIds() {
    return Collections.unmodifiableSet(channelIds);
  }

  public void joinChannel(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    if (channelIds.add(channelId)) {
      touch();
    }
  }

  public void leaveChannel(UUID channelId) {
    if (channelIds.remove(channelId)) {
      touch();
    }
  }

  public boolean isMemberOfChannel(UUID channelId) {
    return channelIds.contains(channelId);
  }

  @Override
  public String toString() {
    return "User[id=%s, username=%s]"
        .formatted(getId(), username);
  }
}
