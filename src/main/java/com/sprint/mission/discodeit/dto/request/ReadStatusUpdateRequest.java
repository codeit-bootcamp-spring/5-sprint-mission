package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ReadStatusUpdateRequest", description = "수정할 읽음 상태 정보")
public record ReadStatusUpdateRequest(
        @Schema(description = "새 마지막 읽은 시각", format = "date-time")
        Instant newLastReadAt
) {
}
