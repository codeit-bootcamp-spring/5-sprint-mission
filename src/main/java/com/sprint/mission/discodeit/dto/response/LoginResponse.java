package com.sprint.mission.discodeit.dto.response;
import java.util.*;
import com.sprint.mission.discodeit.entity.User;

public record LoginResponse(
        UUID userId,
        String username,
        String email
) {
}
