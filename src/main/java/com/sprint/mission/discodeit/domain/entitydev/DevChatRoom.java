package com.sprint.mission.discodeit.domain.entitydev;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class DevChatRoom extends DevBaseEntity {

    private final UUID channel;
    private final UUID guild;

    private final Set<UUID> messages = new LinkedHashSet<>();
    private final Set<UUID> participants = new HashSet<>();

    private int participantsHashcode;

    private static final int DM_MIN = 2;
    private static final int DM_MAX = 10;

    public DevChatRoom(UUID channel, UUID guild) {
        this.channel = Objects.requireNonNull(channel, "Channel id must not be null.");
        this.guild = Objects.requireNonNull(guild, "Guild id must not be null.");
        touch();
    }

    public DevChatRoom(Set<UUID> participants) {
        Objects.requireNonNull(participants, "Participant ids must not be null.");
        if (participants.size() < DM_MIN || participants.size() > DM_MAX)
            throw new IllegalArgumentException("DM ChatRoom requires 2 to 10 participants.");
        if (participants.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("Participant id must not be null.");
        this.participants.addAll(participants);
        this.channel = null;
        this.guild = null;
        this.participantsHashcode = computeParticipantsHashcode(participants);
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
        return channel != null;
    }

    public Set<UUID> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    public void addMessage(UUID message) {
        Objects.requireNonNull(message, "Message id must not be null.");
        if (messages.add(message)) {
            touch();
        }
    }

    public void removeMessage(UUID message) {
        Objects.requireNonNull(message, "Message id must not be null.");
        if (messages.remove(message)) {
            touch();
        }
    }

    public Set<UUID> getParticipants() {
        assertDmOnly("getParticipants");
        return Collections.unmodifiableSet(participants);
    }

    public void addParticipant(UUID user) {
        assertDmOnly("addParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (participants.size() >= DM_MAX && !participants.contains(user))
            throw new IllegalStateException("DM ChatRoom allows up to 10 participants.");
        if (participants.add(user)) {
            participantsHashcode = computeParticipantsHashcode(participants);
            touch();
        }
    }

    public void removeParticipant(UUID user) {
        assertDmOnly("removeParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (!participants.contains(user)) return;
        if (participants.size() <= DM_MIN)
            throw new IllegalStateException("DM ChatRoom must have at least 2 participants.");
        if (participants.remove(user)) {
            participantsHashcode = computeParticipantsHashcode(participants);
            touch();
        }
    }

    public boolean isParticipant(UUID user) {
        assertDmOnly("isParticipant");
        return participants.contains(Objects.requireNonNull(user, "User id must not be null."));
    }

    @Override
    public String toString() {
        return String.format("ChatRoom[id=%s, isChannel=%s, participants=%d, messages=%d]",
                getId(), isChannelChatRoom(), participants.size(), messages.size());
    }
}
