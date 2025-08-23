package com.sprint.mission.discodeit.dto.request.channel;

import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_DESCRIPTION_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MAX_CHANNEL_NAME_LENGTH;
import static com.sprint.mission.discodeit.support.Constants.MIN_CHANNEL_NAME_LENGTH;

import jakarta.validation.constraints.Size;

public record PublicChannelUpdateRequest(

    @Size(min = MIN_CHANNEL_NAME_LENGTH, max = MAX_CHANNEL_NAME_LENGTH)
    String newName,

    @Size(max = MAX_CHANNEL_DESCRIPTION_LENGTH)
    String newDescription
) {

}
