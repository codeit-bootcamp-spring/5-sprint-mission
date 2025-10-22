package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "채널에는 이름이 존재해야 합니다.")
    @Size(min = 2, max = 20, message = "채널의 이름은 최소 2자, 최대 20자 이내여야 합니다.")
    String newName,

    @NotBlank(message = "채널에는 해당 채널의 설명이 존재해야 합니다.")
    @Size(max = 100, message = "채널의 설명을 100자 이하로 입력해주세요.")
    String newDescription
) {

}
