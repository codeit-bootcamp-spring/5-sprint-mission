package com.sprint.mission.discodeit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "MessageCreateRequest", description = "Message 생성 정보")
public record MessageCreateRequest(
        @Schema(description = "메시지 내용")
        String content,
        @Schema(description = "Channel ID", format = "uuid")
        UUID channelId,
        @Schema(description = "작성자 User ID", format = "uuid")
        UUID authorId
) {
}
