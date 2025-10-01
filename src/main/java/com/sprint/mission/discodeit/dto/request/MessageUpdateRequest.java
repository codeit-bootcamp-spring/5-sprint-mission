package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "메시지 내용은 필수입니다")
    // 길이 제한해야 되는 거 아닌가?
    String newContent
) {

}
