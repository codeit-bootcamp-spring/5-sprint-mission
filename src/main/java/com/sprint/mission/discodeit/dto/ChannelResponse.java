package com.sprint.mission.discodeit.dto;

import lombok.Builder;

import java.util.UUID;

public class ChannelResponse {

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
