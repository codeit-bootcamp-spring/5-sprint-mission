package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(name = "PrivateChannelCreateRequest", description = "Private Channel 생성 정보")
public record PrivateChannelCreateRequest(
        @Schema(description = "참여자 User ID 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        List<UUID> participantIds // 필수가 아니던가..?? 뭐지
) {
}
