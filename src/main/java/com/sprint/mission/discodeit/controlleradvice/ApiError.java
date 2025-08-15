package com.sprint.mission.discodeit.controlleradvice;

import java.util.List;

public record ApiError(String code, String message, List<String> errors) {
}
