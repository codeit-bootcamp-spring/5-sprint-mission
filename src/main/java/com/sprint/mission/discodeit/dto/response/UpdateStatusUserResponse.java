package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record UpdateStatusUserResponse(UUID userId, boolean status) {
}
