package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {

    @Getter
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class CreateRequest {
        ChannelType type;
        String name;
        String description;
        UUID adminUserId;
        UUID userId;
    }

    @Getter
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class UpdateRequest {
        UUID id;
        String name;
        String description;
    }

    @Getter
    @RequiredArgsConstructor
    @Builder
    public static class DetailResponse {
        UUID id;
        String name;
        String description;
        Instant lastMessageCreatedAt;
        List<UUID> userIds;
    }
}
