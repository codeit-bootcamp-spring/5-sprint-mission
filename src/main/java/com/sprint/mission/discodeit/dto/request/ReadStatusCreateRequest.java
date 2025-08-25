package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(name = "ReadStatusCreateRequest", description = "Message 읽음 상태 생성 정보")
public record ReadStatusCreateRequest(
        @Schema(description = "User ID", format = "uuid")
        UUID userId,
        @Schema(description = "Channel ID", format = "uuid")
        UUID channelId,
        @Schema(description = "마지막 읽은 시각", format = "date-time")
        Instant lastReadAt
) {
}
