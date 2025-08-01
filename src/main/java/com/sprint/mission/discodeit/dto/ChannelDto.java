package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChannelDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class CreateRequest {
        ChannelType type;
        String name;
        String description;
        UUID adminUserId;
        UUID userId;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class UpdateRequest {
        UUID id;
        String name;
        String description;
    }

    @Getter
    @Setter
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
