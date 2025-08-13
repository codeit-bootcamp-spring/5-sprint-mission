package com.sprint.mission.discodeit.dto.request;

public record UserUpdateProfileSettingsRequest(
        String globalName,
        String bio
) {
}
