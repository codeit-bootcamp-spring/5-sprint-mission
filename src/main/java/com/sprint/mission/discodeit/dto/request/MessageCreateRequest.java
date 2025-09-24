package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "메세지의 내용을 입력해 주세요.")
    @Size(min = 1, message = "메세지는 최소 1자 이상 입력해야 합니다.")
    String content,

    @NotNull(message = "메세지를 보내기 위해선 채널이 존재해야 합니다.")
    UUID channelId,

    @NotNull(message = "메세지를 보낼 사용자가 존재해야 합니다.")
    UUID authorId
) {

}
