package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
        @Size(max = 1000, message = "메시지 내용은 최대 1000자까지 가능합니다.")
        String content,

        @NotNull(message = "채널 ID는 반드시 필요합니다.")
        UUID channelId,

        @NotNull(message = "작성자 ID는 반드시 필요합니다.")
        UUID authorId
) {

}
