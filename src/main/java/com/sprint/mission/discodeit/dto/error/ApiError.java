package com.sprint.mission.discodeit.dto.error;

import java.util.List;

public record ApiError(String code, String message, List<String> errors) {
}
