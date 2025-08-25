package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MessageUpdateRequest", description = "수정할 Message 내용")
public record MessageUpdateRequest(
        @Schema(description = "새 메시지 내용")
        String newContent
) {
}
