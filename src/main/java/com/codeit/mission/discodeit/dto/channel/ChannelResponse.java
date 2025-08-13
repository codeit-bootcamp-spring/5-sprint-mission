package com.codeit.mission.discodeit.dto.channel;

import com.codeit.mission.discodeit.entity.Channel;
import com.codeit.mission.discodeit.entity.ChannelType;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class ChannelResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final ChannelType type;
    private final String name;
    private final String description;

    private final Instant lastMessageTime;
    private final List<UUID> participantUserIds;

    public ChannelResponse(Channel channel, Instant lastMessageTime, List<UUID> participantUserIds) {
        this.id = channel.getId();
        this.createdAt = channel.getCreatedAt();
        this.updatedAt = channel.getUpdatedAt();
        this.type = channel.getType();
        this.name = channel.getName();
        this.description = channel.getDescription();
        this.lastMessageTime = lastMessageTime;
        this.participantUserIds = participantUserIds;
    }

    public ChannelResponse(Channel channel, Instant lastMessageTime) {
        this(channel, lastMessageTime, null);
    }
}
