package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;

import java.util.UUID;

public class ChannelResponse {

    @Builder
    public record detail(
            UUID id,
            ChannelType type,
            String name,
            String topic,
            String description,
            String createdAt,
            String updatedAt
    ) {}

    @Builder
    public record summary(
            UUID channelId,
            String name,
            String topic
    ) {}

    @Builder
    public record join(
            UUID userId,
            UUID channelId,
            String message
    ){}
}
