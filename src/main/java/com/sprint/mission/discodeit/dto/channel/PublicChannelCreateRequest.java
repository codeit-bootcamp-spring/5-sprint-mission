package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotBlank;

public record PublicChannelCreateRequest(

    @NotBlank
    String name,

    String description
) {

}
