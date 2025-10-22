package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(
        @NotBlank(message = "채널명은 필수입니다.")
        @Size(min = 1, max = 100, message = "채널명은 1자 이상 100자 이하여야 합니다.")
        String name,

        @Size(max = 500, message = "채널 설명은 500자 이하여야 합니다.")
        String description
) {

}
