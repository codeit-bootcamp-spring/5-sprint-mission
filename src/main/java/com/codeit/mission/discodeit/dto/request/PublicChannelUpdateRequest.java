package com.codeit.mission.discodeit.dto.request;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
        @Size(min = 1, max = 100, message = "새 채널명은 1자 이상 100자 이하여야 합니다.")
        String newName,

        @Size(max = 500, message = "새 채널 설명은 500자 이하여야 합니다.")
        String newDescription
) {

}
