package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageUpdateRequest(
    String content
) {

}
