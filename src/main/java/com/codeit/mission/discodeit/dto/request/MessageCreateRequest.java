package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
        @NotBlank(message = "메시지 내용은 필수입니다.")
        @Size(min = 1, max = 2000, message = "메시지는 1자 이상 2000자 이하여야 합니다.")
        String content,

        @NotNull(message = "채널 ID는 필수입니다.")
        UUID channelId,

        @NotNull(message = "작성자 ID는 필수입니다.")
        UUID authorId
) {

}
