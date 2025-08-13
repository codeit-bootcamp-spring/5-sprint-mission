package com.sprint.mission.discodeit.domain.entityprod;

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
public class ProdChatRoom extends ProdBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private ProdChannel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guild_id")
    private ProdGuild guild;

    @ManyToMany(mappedBy = "chatRooms")
    private Set<ProdUser> participants = new HashSet<>();

    @Column(name = "participants_hashcode")
    private int participantsHashcode;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProdMessage> messages = new LinkedHashSet<>();

    private static final int DM_MIN = 2;
    private static final int DM_MAX = 10;

    public ProdChatRoom(ProdChannel channel, ProdGuild guild) {
        this.channel = Objects.requireNonNull(channel, "Channel must not be null.");
        this.guild = Objects.requireNonNull(guild, "Guild must not be null.");
    }

    public ProdChatRoom(Set<ProdUser> participants) {
        Objects.requireNonNull(participants, "Participants must not be null.");
        if (participants.size() < 2 || participants.size() > 10)
            throw new IllegalArgumentException("DM ChatRoom requires 2 to 10 participants.");
        this.participants.addAll(participants);
        this.participantsHashcode = computeParticipantsHashcode(participants);
    }

    public static int computeParticipantsHashcode(Set<ProdUser> participants) {
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

    public Set<ProdMessage> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    public void addMessage(ProdMessage jpaMessage) {
        messages.add(Objects.requireNonNull(jpaMessage, "Message must not be null."));
    }

    public void removeMessage(ProdMessage jpaMessage) {
        messages.remove(Objects.requireNonNull(jpaMessage, "Message must not be null."));
    }

    public Set<ProdUser> getParticipants() {
        if (isChannelChatRoom())
            throw new UnsupportedOperationException("채널 기반 ChatRoom은 GuildService로 멤버를 조회하세요.");
        return Collections.unmodifiableSet(participants);
    }

    public void addParticipant(ProdUser jpaUser) {
        assertDmOnly("addParticipant");
        Objects.requireNonNull(jpaUser, "User id must not be null.");
        if (participants.size() >= DM_MAX && !participants.contains(jpaUser))
            throw new IllegalStateException("DM ChatRoom allows up to 10 participants.");
        if (participants.add(jpaUser)) participantsHashcode = computeParticipantsHashcode(participants);
    }

    public void removeParticipant(ProdUser jpaUser) {
        assertDmOnly("removeParticipant");
        Objects.requireNonNull(jpaUser, "User id must not be null.");
        if (!participants.contains(jpaUser)) return;
        if (participants.size() <= DM_MIN)
            throw new IllegalStateException("DM ChatRoom must have at least 2 participants.");
        if (participants.remove(jpaUser)) participantsHashcode = computeParticipantsHashcode(participants);
    }

    public boolean isParticipant(ProdUser jpaUser) {
        assertDmOnly("isParticipant");
        return participants.contains(Objects.requireNonNull(jpaUser));
    }

    @Override
    public String toString() {
        return String.format("ChatRoom[id=%s, isChannel=%s, participants=%d, messages=%d]",
                getId(), isChannelChatRoom(), participants.size(), messages.size());
    }
}
