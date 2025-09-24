package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(
        @NotBlank(message = "새 메시지 내용은 필수입니다.")
        @Size(min = 1, max = 2000, message = "새 메시지는 1자 이상 2000자 이하여야 합니다.")
        String newContent
) {

}
