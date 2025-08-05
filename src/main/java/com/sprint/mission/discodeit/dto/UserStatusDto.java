package com.sprint.mission.discodeit.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {

    @Getter
    @Builder
    public static class CreateRequest {
        private UUID userId;
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private UUID id;
        private UUID userId;
        private Instant lastLogin;
    }
}
