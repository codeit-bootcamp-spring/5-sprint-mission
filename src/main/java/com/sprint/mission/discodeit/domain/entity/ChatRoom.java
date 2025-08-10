package com.sprint.mission.discodeit.domain.entity;

import com.sprint.mission.discodeit.domain.entity.guild.Guild;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "chat_rooms",
        indexes = {
                @Index(name = "idx_chatroom_participants_hash", columnList = "participants_hashcode")
        }
)
public class ChatRoom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private Guild guild;

    @ManyToMany(mappedBy = "chatRooms")
    private Set<User> participants = new HashSet<>();

    @Column(name = "participants_hashcode")
    private int participantsHashcode;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Message> messages = new LinkedHashSet<>();

    private static final int DM_MIN = 2;
    private static final int DM_MAX = 10;

    public ChatRoom(Channel channel, Guild guild) {
        this.channel = Objects.requireNonNull(channel, "Channel must not be null.");
        this.guild = Objects.requireNonNull(guild, "Guild must not be null.");
    }

    public ChatRoom(Set<User> participants) {
        Objects.requireNonNull(participants, "Participants must not be null.");
        if (participants.size() < 2 || participants.size() > 10)
            throw new IllegalArgumentException("DM ChatRoom requires 2 to 10 participants.");
        this.participants.addAll(participants);
        this.participantsHashcode = computeParticipantsHashcode(participants);
    }

    public static int computeParticipantsHashcode(Set<User> participants) {
        return participants.stream()
                .map(p -> p.getId().toString())
                .sorted()
                .collect(Collectors.joining("|"))
                .hashCode();
    }

    public static int computeParticipantsHashcodeById(Set<UUID> participants) {
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

    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    public void addMessage(Message message) {
        messages.add(Objects.requireNonNull(message, "Message must not be null."));
    }

    public void removeMessage(Message message) {
        messages.remove(Objects.requireNonNull(message, "Message must not be null."));
    }

    public Set<User> getParticipants() {
        if (isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService로 멤버를 조회하세요.");
        return Collections.unmodifiableSet(participants);
    }

    public void addParticipant(User user) {
        assertDmOnly("addParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (participants.size() >= DM_MAX && !participants.contains(user))
            throw new IllegalStateException("DM ChatRoom allows up to 10 participants.");
        if (participants.add(user)) participantsHashcode = computeParticipantsHashcode(participants);
    }

    public void removeParticipant(User user) {
        assertDmOnly("removeParticipant");
        Objects.requireNonNull(user, "User id must not be null.");
        if (!participants.contains(user)) return;
        if (participants.size() <= DM_MIN)
            throw new IllegalStateException("DM ChatRoom must have at least 2 participants.");
        if (participants.remove(user)) participantsHashcode = computeParticipantsHashcode(participants);
    }

    public boolean isParticipant(User user) {
        assertDmOnly("isParticipant");
        return participants.contains(Objects.requireNonNull(user));
    }

    @Override
    public String toString() {
        return String.format("ChatRoom[id=%s, isChannel=%s, participants=%d, messages=%d]",
                getId(), isChannelChatRoom(), participants.size(), messages.size());
    }
}
