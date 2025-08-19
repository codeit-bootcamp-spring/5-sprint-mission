package com.sprint.mission.discodeit.dto.request.chnanel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

    @NotBlank
    @Size(min = 1, max = 100)
    String name,

    @Size(max = 1024)
    String description
) {

}
