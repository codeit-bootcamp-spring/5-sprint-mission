package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {

    @Getter
    @Builder
    public static class CreateRequest {
        ChannelType type;
        String name;
        String description;
        UUID adminUserId;
        UUID userId;
    }

    @Getter
    @Builder
    public static class UpdateRequest {
        UUID id;
        String name;
        String description;
        UUID userId;
    }

    @Getter
    @Builder
    @ToString
    public static class DetailResponse {
        UUID id;
        String name;
        String description;
        Instant lastMessageCreatedAt;
        List<UUID> userIds;
    }
}
