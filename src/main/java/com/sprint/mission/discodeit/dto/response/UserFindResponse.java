package com.sprint.mission.discodeit.dto.response;

import java.util.*;

public record UserFindResponse(
        UUID id,
        String username,
        String email,
        boolean status //
) {
}
