package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(name = "ChannelDto")
public record ChannelDto(
        @Schema(description = "Channel ID", format = "uuid")
        UUID id,
        @Schema(description = "채널 타입", allowableValues = {"PUBLIC", "PRIVATE"})
        ChannelType type,
        @Schema(description = "채널명")
        String name,
        @Schema(description = "채널 설명")
        String description,
        @Schema(description = "참여자 User ID 목록")
        List<UUID> participantIds,
        @Schema(description = "마지막 메시지 시각", format = "date-time")
        Instant lastMessageAt
) {
}
