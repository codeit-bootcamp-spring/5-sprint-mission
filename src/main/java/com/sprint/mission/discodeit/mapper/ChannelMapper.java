package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.domain.entity.Channel;
import com.sprint.mission.discodeit.dto.response.channel.ChannelResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChannelMapper {

    public static ChannelResponse toChannelResponse(Channel ch) {
        if (ch == null) return null;

        Boolean isSecret = ch.isPrivate() ? null : ch.isSecretGuildChannel();
        UUID guildId = ch.isPrivate() ? null : ch.getGuildId();

        return new ChannelResponse(
                ch.getId(),
                ch.getCreatedAt(),
                ch.getUpdatedAt(),
                ch.getName(),
                ch.getType(),
                ch.isPrivate(),
                isSecret,
                guildId,
                ch.getMemberIds(),
                ch.activeParticipantCount()
        );
    }

    public static List<ChannelResponse> toChannelResponses(Collection<Channel> channels) {
        if (channels == null || channels.isEmpty()) return List.of();
        return channels.stream()
                .map(ChannelMapper::toChannelResponse)
                .toList();
    }
}
