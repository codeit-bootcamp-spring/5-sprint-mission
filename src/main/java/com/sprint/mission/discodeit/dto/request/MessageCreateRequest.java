package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메시지 내용은 필수입니다")
    // 길이 제한해야 되는 거 아닌가?
    String content,

    @NotNull(message = "채널 ID는 필수입니다")
    UUID channelId,


    @NotNull(message = "작성자 ID는 필수입니다")
    UUID authorId
) {

}
