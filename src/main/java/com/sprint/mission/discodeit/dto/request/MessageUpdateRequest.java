package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
    @NotBlank(message = "메세지의 내용을 입력해 주세요.")
    @Size(min = 1, message = "메세지는 최소 1자 이상 입력해야 합니다.")
    String newContent
) {

}
