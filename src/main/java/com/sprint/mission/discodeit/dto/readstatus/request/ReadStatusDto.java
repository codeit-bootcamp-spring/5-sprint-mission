package com.sprint.mission.discodeit.dto.readstatus.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
        @NotNull(message = "사용자 아이디를 입력하세요")
        UUID userId,
        @NotNull(message = "채널 아이디를 입력하세요")
        UUID channelId,
        @NotNull(message = "메시지 아이디를 입력하세요")
        UUID messageId
) {
}
