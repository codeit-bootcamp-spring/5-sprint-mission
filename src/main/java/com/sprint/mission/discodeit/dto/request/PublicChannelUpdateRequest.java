package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "채널 이름은 필수입니다.")
    @Size(min = 2, max = 15, message = "채널 설명은 2자 이상 15자 이하여야합니다")
    String newName,

    @NotBlank(message = "채널 설명은 필수입니다.")
    @Size(min = 2, max = 15, message = "채널 설명은 2자 이상 15자 이하여야합니다")
    String newDescription
) {

}
