package com.sprint.mission.discodeit.dto.message;

import static com.sprint.mission.discodeit.support.Constants.MAX_MESSAGE_CONTENT_LENGTH;

import jakarta.validation.constraints.Size;

public record MessageUpdateRequest(

    @Size(max = MAX_MESSAGE_CONTENT_LENGTH)
    String newContent
) {

}
