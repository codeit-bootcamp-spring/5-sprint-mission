package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
        @NotBlank(message = "수정할 내용은 비어 있을 수 없습니다.")
        @Size(max = 1000, message = "메시지 내용은 최대 1000자까지 가능합니다.")
        String newContent
) {

}
