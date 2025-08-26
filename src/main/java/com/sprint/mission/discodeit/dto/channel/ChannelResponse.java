package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelResponse {

    @Builder
    public record Detail(
            UUID id,
            ChannelType type,
            String name,
            String description,
            String createdAt,
            String updatedAt
    ) {
        public static Detail from (Channel channel) {
            return Detail.builder()
                    .id(channel.getId())
                    .name(channel.getName())
                    .type(channel.getType())
                    .description(channel.getDescription())
                    .createdAt(channel.getCreatedAtFormatted())
                    .updatedAt(channel.getUpdatedAtFormatted())
                    .build();
        }
    }

    @Builder
    public record Summary(
            UUID channelId,
            String name,
            ChannelType type
    ) {
        public static Summary from (Channel channel) {
            return Summary.builder()
                    .channelId(channel.getId())
                    .name(channel.getName())
                    .type(channel.getType())
                    .build();
        }
    }

    @Builder
    public record JoinedChannels(
            UUID id,
            ChannelType type,
            String name,
            String description,
            List<UUID> participantIds,
            Instant lastMessageAt
    ) {
        public static JoinedChannels  from (Channel channel, ReadStatus readStatus, List<UUID> participantIds) {
            return JoinedChannels.builder()
                    .id(channel.getId())
                    .type(channel.getType())
                    .name(channel.getName())
                    .description(channel.getDescription())
                    .participantIds(participantIds)
                    .lastMessageAt(readStatus.getLastReadAt())
                    .build();
        }
    }

    @Builder
    public record Join(
            UUID userId,
            UUID channelId,
            String message
    ){}
}
