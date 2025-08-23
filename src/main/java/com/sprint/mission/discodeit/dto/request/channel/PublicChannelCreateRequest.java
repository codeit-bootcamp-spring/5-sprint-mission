package com.sprint.mission.discodeit.dto.request.channel;

import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_DESCRIPTION_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_NAME_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_CHANNEL_NAME_LENGTH;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicChannelCreateRequest(

    @NotBlank
    @Size(min = MIN_CHANNEL_NAME_LENGTH, max = MAX_CHANNEL_NAME_LENGTH)
    String name,

    @Size(max = MAX_CHANNEL_DESCRIPTION_LENGTH)
    String description
) {

}
