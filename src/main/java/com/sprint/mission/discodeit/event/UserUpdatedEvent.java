package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.UserDTO;

public record UserUpdatedEvent(
    String name,
    UserDTO userDTO
) {

}
