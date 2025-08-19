package com.sprint.mission.discodeit.domain.entity;

import static com.sprint.mission.discodeit.support.StringUtil.digitsOnlyOrNull;
import static com.sprint.mission.discodeit.support.StringUtil.normalizeEmail;
import static com.sprint.mission.discodeit.support.StringUtil.normalizeUsername;
import static com.sprint.mission.discodeit.support.StringUtil.nullOrStrip;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
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
  private LocalDate birthDate;
  private boolean subscribedToNewsletter;
  private String globalName;
  private String bio;
  private String phoneNumber;

  private boolean verified;
  private boolean deactivated;
  private boolean banned;

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
    this.email = normalizeEmail(Objects.requireNonNull(email, "email must not be null"));
    this.username = normalizeUsername(
        Objects.requireNonNull(username, "username must not be null"));
    this.password = Objects.requireNonNull(password, "password must not be null");
    this.profileId = profileId;
  }

  public void changeEmail(String email) {
    String v = normalizeEmail(Objects.requireNonNull(email, "email must not be null"));
    assignIfChanged(v, () -> this.email, x -> this.email = x);
  }

  public void changeUsername(String username) {
    String v = normalizeUsername(Objects.requireNonNull(username, "username must not be null"));
    assignIfChanged(v, () -> this.username, x -> this.username = x);
  }

  public void changePassword(String password) {
    String v = Objects.requireNonNull(password, "password must not be null");
    assignIfChanged(v, () -> this.password, x -> this.password = x);
  }

  public boolean matchesPassword(String raw, PasswordEncoder encoder) {
    return encoder.matches(raw, this.password);
  }

  public void changeGlobalName(String globalName) {
    String v = (globalName == null || globalName.isBlank()) ? null : globalName.strip();
    assignIfChanged(v, () -> this.globalName, x -> this.globalName = x);
  }

  public void changeBirthDate(LocalDate birthDate) {
    assignIfChanged(birthDate, () -> this.birthDate, x -> this.birthDate = x);
  }

  public void changeBio(String bio) {
    String v = nullOrStrip(bio);
    assignIfChanged(v, () -> this.bio, x -> this.bio = x);
  }

  public void changePhoneNumber(String phoneNumber) {
    String v = digitsOnlyOrNull(phoneNumber);
    assignIfChanged(v, () -> this.phoneNumber, x -> this.phoneNumber = x);
  }

  public void clearPhoneNumber() {
    clearIfPresent(() -> this.phoneNumber, () -> this.phoneNumber = null);
  }

  public void setSubscribedToNewsletter(boolean subscribed) {
    if (this.subscribedToNewsletter != subscribed) {
      this.subscribedToNewsletter = subscribed;
      touch();
    }
  }

  public void changeProfileId(UUID profileId) {
    UUID v = Objects.requireNonNull(profileId, "profileId must not be null");
    assignIfChanged(v, () -> this.profileId, x -> this.profileId = x);
  }

  public void clearProfileId() {
    clearIfPresent(() -> this.profileId, () -> this.profileId = null);
  }

  public void verify() {
    setFlagIfNeeded(true, () -> this.verified, x -> this.verified = x);
  }

  public void unverify() {
    setFlagIfNeeded(false, () -> this.verified, x -> this.verified = x);
  }

  public void deactivate() {
    setFlagIfNeeded(true, () -> this.deactivated, x -> this.deactivated = x);
  }

  public void activate() {
    setFlagIfNeeded(false, () -> this.deactivated, x -> this.deactivated = x);
  }

  public void ban() {
    setFlagIfNeeded(true, () -> this.banned, x -> this.banned = x);
  }

  public void unban() {
    setFlagIfNeeded(false, () -> this.banned, x -> this.banned = x);
  }

  public boolean isActive() {
    return !(deactivated || banned || isDeleted());
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
    return "User[id=%s, email=%s, username=%s, globalName=%s, active=%s]"
        .formatted(getId(), email, username, globalName, isActive());
  }

  private <T> void assignIfChanged(T newValue, Supplier<T> getter, Consumer<T> writer) {
    T current = getter.get();
    if (!Objects.equals(current, newValue)) {
      writer.accept(newValue);
      touch();
    }
  }

  private void clearIfPresent(Supplier<?> getter, Runnable clearer) {
    if (getter.get() != null) {
      clearer.run();
      touch();
    }
  }

  private void setFlagIfNeeded(boolean desired, Supplier<Boolean> getter,
      Consumer<Boolean> setter) {
    if (getter.get() != desired) {
      setter.accept(desired);
      touch();
    }
  }
}
