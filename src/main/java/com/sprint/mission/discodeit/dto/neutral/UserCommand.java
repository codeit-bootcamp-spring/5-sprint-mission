package com.sprint.mission.discodeit.dto.neutral;

import java.util.Optional;

public record UserCommand(
    String username,
    String email,
    String password,
    Optional<NewBinaryContent> profile
) {

}
