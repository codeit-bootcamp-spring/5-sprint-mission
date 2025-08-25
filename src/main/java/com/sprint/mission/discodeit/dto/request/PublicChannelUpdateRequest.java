package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicChannelUpdateRequest", description = "수정할 Channel 정보")
public record PublicChannelUpdateRequest(
        @Schema(description = "새 채널명")
        String newName,
        @Schema(description = "새 채널 설명")
        String newDescription
) {
}
