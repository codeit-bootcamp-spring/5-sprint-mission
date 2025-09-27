package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message=" 채널 이름은 필수입니다.")
    @Size(max=100, message = "100자 이하여야 합니다.")
    String newName,
    @Size(max=100, message = "500자 이하여야 합니다.")
    String newDescription
) {

}
