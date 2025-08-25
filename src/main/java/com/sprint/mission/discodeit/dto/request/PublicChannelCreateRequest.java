package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PublicChannelCreateRequest", description = "Public Channel 생성 정보")
public record PublicChannelCreateRequest(
        @Schema(description = "채널명")
        String name,
        @Schema(description = "채널 설명")
        String description
) {
}
