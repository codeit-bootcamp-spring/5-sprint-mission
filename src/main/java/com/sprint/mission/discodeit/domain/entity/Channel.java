package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.enums.ChannelType;
import com.sprint.mission.discodeit.support.Validators;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class Channel extends AbstractEntity {

  private static final int DM_MIN = 2;
  private static final int DM_MAX = 10;

  private String name;
  private ChannelType type;
  private final boolean isPrivate;
  private final UUID guildId;
  private final Boolean isSecret;

  private final Set<UUID> memberIds = new HashSet<>();
  private final Set<UUID> messageIds = new LinkedHashSet<>();
  private final Set<UUID> activeParticipantIds = new LinkedHashSet<>();

  private Channel(UUID guildId, boolean isPrivate, Boolean isSecret, String name, ChannelType type,
      Set<UUID> initialMembers) {
    this.isPrivate = isPrivate;
    this.guildId = isPrivate ? null
        : Objects.requireNonNull(guildId, "guildId must not be null for guild channel.");
    this.isSecret = isPrivate ? null : normalizeSecret(isSecret);
    setName(name);
    setType(type);
    if (initialMembers != null && !initialMembers.isEmpty()) {
      addMembers(initialMembers);
    }
    validateInvariants();
  }

  public static Channel createDm(String name, ChannelType type, Set<UUID> participants) {
    requireDmParticipants(participants);
    return new Channel(null, true, null, name, type, participants);
  }

  public static Channel createGuildChannel(
      UUID guildId, String name, ChannelType type, Boolean isSecret,
      Set<UUID> initialAllowedMembers) {
    if (Boolean.TRUE.equals(isSecret) && (initialAllowedMembers == null
        || initialAllowedMembers.isEmpty())) {
      throw new IllegalArgumentException(
          "Secret guild channel requires at least one allowed member.");
    }
    if (!Boolean.TRUE.equals(isSecret) && initialAllowedMembers != null
        && !initialAllowedMembers.isEmpty()) {
      throw new IllegalArgumentException(
          "Public guild channel must not have allowed members list.");
    }
    return new Channel(guildId, false, isSecret, name, type, initialAllowedMembers);
  }

  public void setName(String name) {
    String v = Validators.validateChannelName(name);
    if (!Objects.equals(this.name, v)) {
      this.name = v;
      touch();
    }
  }

  public void setType(ChannelType type) {
    Objects.requireNonNull(type, "Channel type must not be null");
    if (this.type != type) {
      this.type = type;
      touch();
    }
  }

  public boolean isGuildChannel() {
    return !isPrivate;
  }

  public boolean isSecretGuildChannel() {
    return isGuildChannel() && Boolean.TRUE.equals(isSecret);
  }

  public boolean isPublicGuildChannel() {
    return isGuildChannel() && !Boolean.TRUE.equals(isSecret);
  }

  public Set<UUID> getMemberIds() {
    return Collections.unmodifiableSet(memberIds);
  }

  public boolean isMember(UUID userId) {
    return memberIds.contains(requireUserId(userId));
  }

  public void addMember(UUID userId) {
    assertMembershipAllowed("addMember");
    boolean changed = memberIds.add(requireUserId(userId));
    if (changed) {
      if (isPrivate) {
        assertDmCardinality();
      }
      touch();
    }
  }

  public void addMembers(Collection<UUID> userIds) {
    assertMembershipAllowed("addMembers");
    Objects.requireNonNull(userIds, "userIds must not be null");
    boolean changed = false;
    for (UUID id : userIds) {
      if (memberIds.add(requireUserId(id))) {
        changed = true;
      }
    }
    if (changed) {
      if (isPrivate) {
        assertDmCardinality();
      }
      touch();
    }
  }

  public void removeMember(UUID userId) {
    assertMembershipAllowed("removeMember");
    UUID uid = requireUserId(userId);
    boolean removed = memberIds.remove(uid);
    boolean removedActive = activeParticipantIds.remove(uid);
    if (removed || removedActive) {
      if (isPrivate) {
        assertDmCardinality();
      }
      touch();
    }
  }

  public void removeMembers(Collection<UUID> userIds) {
    assertMembershipAllowed("removeMembers");
    Objects.requireNonNull(userIds, "userIds must not be null");
    boolean changed = false;
    for (UUID id : userIds) {
      if (memberIds.remove(requireUserId(id))) {
        changed = true;
      }
    }
    if (changed) {
      if (isPrivate) {
        assertDmCardinality();
      }
      touch();
    }
  }

  public void addMessage(UUID messageId) {
    if (memberIds.add(requireMessageId(messageId))) {
      touch();
    }
  }

  public void addMessages(Collection<UUID> messageIds) {
    Objects.requireNonNull(messageIds, "messageIds must not be null");
    boolean changed = false;
    for (UUID id : messageIds) {
      if (memberIds.add(requireUserId(id))) {
        changed = true;
      }
    }
    if (changed) {
      touch();
    }
  }

  public void removeMessage(UUID messageId) {
    if (memberIds.remove(requireMessageId(messageId))) {
      touch();
    }
  }

  public void removeMessages(Collection<UUID> messageIds) {
    Objects.requireNonNull(messageIds, "messageIds must not be null");
    boolean changed = false;
    for (UUID id : messageIds) {
      if (memberIds.remove(requireUserId(id))) {
        changed = true;
      }
    }
    if (changed) {
      touch();
    }
  }

  public Set<UUID> getActiveParticipantIds() {
    return Collections.unmodifiableSet(activeParticipantIds);
  }

  public int activeParticipantCount() {
    return activeParticipantIds.size();
  }

  public void join(UUID userId) {
    assertVoiceOnly("join");
    UUID uid = requireUserId(userId);
    if (isPrivate || isSecretGuildChannel()) {
      if (!memberIds.contains(uid)) {
        throw new IllegalStateException("User is not allowed to join this channel.");
      }
    }
    if (activeParticipantIds.add(uid)) {
      touch();
    }
  }

  public void leave(UUID userId) {
    assertVoiceOnly("leave");
    if (activeParticipantIds.remove(requireUserId(userId))) {
      touch();
    }
  }

  private static void requireDmParticipants(Set<UUID> participants) {
    Objects.requireNonNull(participants, "DM participants must not be null.");
    if (participants.stream().anyMatch(Objects::isNull)) {
      throw new NullPointerException("DM participant id must not be null.");
    }
    int size = participants.size();
    if (size < DM_MIN || size > DM_MAX) {
      throw new IllegalArgumentException("DM requires 2 to 10 participants. Given: " + size);
    }
  }

  private void assertMembershipAllowed(String method) {
    if (isPublicGuildChannel()) {
      throw new UnsupportedOperationException(
          method + " is only allowed for DM or secret guild channels.");
    }
  }

  private void assertVoiceOnly(String method) {
    if (this.type != ChannelType.VOICE) {
      throw new UnsupportedOperationException(method + " is allowed only for VOICE channels.");
    }
  }

  private void assertDmCardinality() {
    int size = memberIds.size();
    if (size < DM_MIN) {
      throw new IllegalStateException("DM must have at least " + DM_MIN + " members.");
    }
    if (size > DM_MAX) {
      throw new IllegalStateException("DM can have at most " + DM_MAX + " members.");
    }
  }

  private void validateInvariants() {
    if (isPrivate) {
      if (guildId != null) {
        throw new IllegalStateException("DM must not have guildId.");
      }
      if (isSecret != null) {
        throw new IllegalStateException("DM must not have isSecret.");
      }
      assertDmCardinality();
    } else {
      if (guildId == null) {
        throw new IllegalStateException("Guild channel requires guildId.");
      }
      if (isPublicGuildChannel() && !memberIds.isEmpty()) {
        throw new IllegalStateException("Public guild channel must not have member whitelist.");
      }
    }
  }

  private static Boolean normalizeSecret(Boolean flag) {
    return (flag != null && flag) ? Boolean.TRUE : null;
  }

  private static UUID requireUserId(UUID userId) {
    return Objects.requireNonNull(userId, "User id must not be null");
  }

  private static UUID requireMessageId(UUID messageId) {
    return Objects.requireNonNull(messageId, "Message id must not be null");
  }

  public String membersFingerprint() {
    if (memberIds.isEmpty()) {
      return "";
    }
    return memberIds.stream()
        .map(UUID::toString)
        .sorted()
        .collect(Collectors.joining("|"));
  }

  @Override
  public String toString() {
    return "Channel[id=%s, name=%s, type=%s, isPrivate=%s, isSecret=%s, guildId=%s, members=%d, active=%d]"
        .formatted(getId(), name, type, isPrivate, isSecret, guildId, memberIds.size(),
            activeParticipantIds.size());
  }
}
