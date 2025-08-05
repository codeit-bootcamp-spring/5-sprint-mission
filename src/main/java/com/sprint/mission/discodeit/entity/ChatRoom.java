package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatRoom extends BaseEntity implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private UUID channelId;
  private UUID guildId;
  private final Set<UUID> participants = new HashSet<>();
  private final List<UUID> messages = new ArrayList<>();

  public ChatRoom(UUID channelId, UUID guildId) {
    if (channelId == null || guildId == null) {
      throw new IllegalArgumentException("Channel 기반 ChatRoom은 channelId와 guildId가 필요합니다.");
    }
    this.channelId = channelId;
    this.guildId = guildId;
  }

  public ChatRoom(Set<UUID> participants) {
    if (participants == null || participants.size() < 2 || participants.size() > 10) {
      throw new IllegalArgumentException("DM은 2~10명의 참여자가 필요합니다.");
    }
    this.participants.addAll(participants);
  }

  public void addMessage(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("Message ID must not be null.");
    }
    messages.add(id);
  }

  public List<UUID> getMessages() {
    return Collections.unmodifiableList(messages);
  }

  public boolean isChannelChatRoom() {
    return channelId != null;
  }

  public UUID getChannelId() {
    return channelId;
  }

  public UUID getGuildId() {
    return guildId;
  }

  public Set<UUID> getParticipants() {
    if (isChannelChatRoom()) {
      throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService로 멤버를 조회하세요.");
    }
    return Collections.unmodifiableSet(participants);
  }

  public void addParticipant(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("User ID must not be null.");
    }
    participants.add(id);
  }

  public void removeParticipant(UUID id) {
    if (id == null) {
      throw new IllegalArgumentException("User ID must not be null.");
    }
    participants.remove(id);
  }

  public boolean isParticipant(UUID id) {
    if (isChannelChatRoom()) {
      throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService로 멤버를 조회하세요.");
    }
    if (id == null) {
      throw new IllegalArgumentException("User ID must not be null.");
    }
    return participants.contains(id);
  }

  public int participantsHashcode() {
    return participants.stream()
        .map(UUID::toString)
        .sorted()
        .collect(Collectors.joining("|"))
        .hashCode();
  }
}
