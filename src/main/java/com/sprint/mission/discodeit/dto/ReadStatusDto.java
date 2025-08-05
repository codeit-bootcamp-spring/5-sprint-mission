package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto
{
    @Getter
    @RequiredArgsConstructor
    public static class CreateRequest {
        UUID userId;
        UUID channelId;
        Instant readAt;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        UUID id;
        UUID userId;
        UUID channelId;
        Instant readAt;
    }
}
