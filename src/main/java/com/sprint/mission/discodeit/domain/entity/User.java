package com.sprint.mission.discodeit.domain.entity;

import static com.sprint.mission.discodeit.support.StringUtil.nullOrStripAndLowerCase;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    this.email = nullOrStripAndLowerCase(Objects.requireNonNull(email, "email must not be null"));
    this.username = nullOrStripAndLowerCase(
        Objects.requireNonNull(username, "username must not be null"));
    this.password = Objects.requireNonNull(password, "password must not be null");
    this.profileId = profileId;
  }

  public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
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
  }

  public boolean matchesPassword(String raw, PasswordEncoder encoder) {
    return encoder.matches(raw, this.password);
  }

  public void clearProfileId() {
    clearIfPresent(() -> this.profileId, () -> this.profileId = null);
  }

  public Set<UUID> getFriendIds() {
    return Collections.unmodifiableSet(friendIds);
  }

  public void addFriend(UUID friend) {
    Objects.requireNonNull(friend, "friendId must not be null");
    if (friend.equals(getId())) {
      throw new IllegalArgumentException("Cannot add self as friend");
    }
    if (friendIds.add(friend)) {
      touch();
    }
  }

  public void removeFriend(UUID friend) {
    Objects.requireNonNull(friend, "friendId must not be null");
    if (friendIds.remove(friend)) {
      touch();
    }
  }

  public boolean isFriend(UUID friend) {
    Objects.requireNonNull(friend, "friendId must not be null");
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
    Objects.requireNonNull(guildId, "guildId must not be null");
    if (guildIds.remove(guildId)) {
      touch();
    }
  }

  public boolean isMemberOfGuild(UUID guildId) {
    Objects.requireNonNull(guildId, "guildId must not be null");
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
    Objects.requireNonNull(channelId, "channelId must not be null");
    if (channelIds.remove(channelId)) {
      touch();
    }
  }

  public boolean isMemberOfChannel(UUID channelId) {
    Objects.requireNonNull(channelId, "channelId must not be null");
    return channelIds.contains(channelId);
  }

  @Override
  public String toString() {
    return "User[id=%s, username=%s, email=%s]"
        .formatted(getId(), username, email);
  }

  private void clearIfPresent(Supplier<?> getter, Runnable clearer) {
    if (getter.get() != null) {
      clearer.run();
      touch();
    }
  }
}
