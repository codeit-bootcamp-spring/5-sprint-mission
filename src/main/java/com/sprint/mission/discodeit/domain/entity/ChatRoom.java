package com.sprint.mission.discodeit.domain.entity;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class ChatRoom extends BaseEntity {

    private final UUID channelId;
    private final UUID guildId;

    private final Set<UUID> messageIds = new LinkedHashSet<>();
    private final Set<UUID> participantIds = new HashSet<>();

    private int participantsHashcode;

    private static final int DM_MIN = 2;
    private static final int DM_MAX = 10;

    public ChatRoom(UUID channelId, UUID guildId) {
        this.channelId = Objects.requireNonNull(channelId, "Channel id must not be null.");
        this.guildId = Objects.requireNonNull(guildId, "Guild id must not be null.");
        touch();
    }

    public ChatRoom(Set<UUID> participantIds) {
        Objects.requireNonNull(participantIds, "Participant ids must not be null.");
        if (participantIds.size() < DM_MIN || participantIds.size() > DM_MAX)
            throw new IllegalArgumentException("DM ChatRoom requires 2 to 10 participants.");
        if (participantIds.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("Participant id must not be null.");
        this.participantIds.addAll(participantIds);
        this.channelId = null;
        this.guildId = null;
        this.participantsHashcode = computeParticipantsHashcode(participantIds);
        touch();
    }

    public static int computeParticipantsHashcode(Set<UUID> participants) {
        Objects.requireNonNull(participants, "participants must not be null");
        if (participants.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("Participant id must not be null.");
        return participants.stream()
                .map(UUID::toString)
                .sorted()
                .collect(Collectors.joining("|"))
                .hashCode();
    }

    private void assertDmOnly(String method) {
        if (isChannelChatRoom())
            throw new UnsupportedOperationException(
                    method + " is available only for DM ChatRoom. Use GuildService for channel-based ChatRoom.");
    }

    public boolean isChannelChatRoom() {
        return channelId != null;
    }

    public Set<UUID> getMessageIds() {
        return Collections.unmodifiableSet(messageIds);
    }

    public void addMessage(UUID message) {
        Objects.requireNonNull(message, "Message id must not be null.");
        if (messageIds.add(message)) {
            touch();
        }
    }

    public void removeMessage(UUID message) {
        Objects.requireNonNull(message, "Message id must not be null.");
        if (messageIds.remove(message)) {
            touch();
        }
    }

    public Set<UUID> getParticipantIds() {
        assertDmOnly("getParticipants");
        return Collections.unmodifiableSet(participantIds);
    }

    public void addParticipant(UUID user) {
        assertDmOnly("addParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (participantIds.size() >= DM_MAX && !participantIds.contains(user))
            throw new IllegalStateException("DM ChatRoom allows up to 10 participants.");
        if (participantIds.add(user)) {
            participantsHashcode = computeParticipantsHashcode(participantIds);
            touch();
        }
    }

    public void removeParticipant(UUID user) {
        assertDmOnly("removeParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (!participantIds.contains(user)) return;
        if (participantIds.size() <= DM_MIN)
            throw new IllegalStateException("DM ChatRoom must have at least 2 participants.");
        if (participantIds.remove(user)) {
            participantsHashcode = computeParticipantsHashcode(participantIds);
            touch();
        }
    }

    public boolean isParticipant(UUID user) {
        assertDmOnly("isParticipant");
        return participantIds.contains(Objects.requireNonNull(user, "User id must not be null."));
    }

    @Override
    public String toString() {
        return String.format("ChatRoom[id=%s, isChannel=%s, participants=%d, messages=%d]",
                getId(), isChannelChatRoom(), participantIds.size(), messageIds.size());
    }
}
